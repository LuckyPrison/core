package com.ulfric.core.control;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MapSubscription;
import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.module.Module;

public class ModuleNotes extends Module {

	public ModuleNotes()
	{
		super("notes", "/note", "1.0.0", "Packet");
	}

	private Map<NoteType, MapSubscription<Document>> documents;

	@Override
	public void onFirstEnable()
	{
		this.documents = Maps.newEnumMap(NoteType.class);

		DataManager manager = DataManager.get();

		DocumentStore store = manager.getEnsuredDatabase("notes");

		for (NoteType type : NoteType.values())
		{
			String name = type.name().replace("_", "").toLowerCase();

			manager.ensureTableCreated(store, name);

			this.documents.put(type, store.document(new DataAddress<>(name, "notes")).subscribe());
		}

		this.addCommand(new CommandNote(this));
	}

	@Override
	public void onModuleEnable()
	{
		for (MapSubscription<Document> subscription : this.documents.values())
		{
			if (subscription.isSubscribed()) continue;

			subscription.subscribe();
		}

		Notes notes = Notes.getInstance();

		for (Entry<NoteType, MapSubscription<Document>> entry : this.documents.entrySet())
		{
			NoteType type = entry.getKey();
			MapSubscription<Document> subscription = entry.getValue();

			Document document = subscription.getValue();

			if (document == null) continue;

			Set<String> keys = document.getKeys(false);

			if (SetUtils.isEmpty(keys)) continue;

			for (String key : keys)
			{
				Document noteDoc = document.getDocument(key);

				Note note = type.fromDocument(noteDoc);

				if (note == null) continue;

				notes.registerNote(note);
			}
		}
	}

	@Override
	public void onModuleDisable()
	{
		for (MapSubscription<Document> subscription : this.documents.values())
		{
			if (!subscription.isSubscribed()) continue;

			subscription.unsubscribe();
		}

		Notes notes = Notes.getInstance();

		for (NoteType type : NoteType.values())
		{
			List<Note> noteTable = notes.getAllNotes(type);

			if (ListUtils.isEmpty(noteTable)) continue;

			this.log("Saving note type " + type.name().toLowerCase() + ", counted: " + noteTable.size());

			MapSubscription<Document> subscription = this.documents.get(type);

			Document document = subscription.getValue();
			MutableDocument mut = null;

			boolean changed = false;

			for (Note note : noteTable)
			{
				if (!note.isNew()) continue;

				if (mut == null)
				{
					changed = true;

					mut = new SimpleDocument(document.deepCopy());
				}

				String notePath = "n" + note.getID();

				MutableDocument noteDoc = mut.getDocument(notePath);

				if (noteDoc == null)
				{
					noteDoc = mut.createDocument(notePath);
				}

				note.into(noteDoc);
			}

			if (!changed) continue;

			subscription.setValue(mut);
		}

		notes.dump();
	}

}