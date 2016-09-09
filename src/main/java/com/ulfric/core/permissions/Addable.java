package com.ulfric.core.permissions;

import java.util.regex.Pattern;

import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Limit;
import com.ulfric.lib.coffee.npermission.Permissible;
import com.ulfric.lib.coffee.npermission.Permission;
import com.ulfric.lib.coffee.numbers.NumberUtils;

interface Addable {

	static Addable valueOf(Object object)
	{
		if (object instanceof Permission)
		{
			return new PermissionAddable((Permission) object);
		}

		if (object instanceof Group)
		{
			return new GroupAddable((Group) object);
		}

		if (object instanceof LimitAddable)
		{
			return (Addable) object;
		}

		throw new IllegalArgumentException(object.getClass().getSimpleName());
	}

	boolean add(Permissible permissible);
	boolean remove(Permissible permissible);
	String observe(Permissible permissible);

	final static class PermissionAddable implements Addable
	{
		PermissionAddable(Permission permission)
		{
			this.permission = permission;
		}

		private final Permission permission;

		@Override
		public boolean add(Permissible permissible)
		{
			return permissible.addPermission(this.permission);
		}

		@Override
		public boolean remove(Permissible permissible)
		{
			return permissible.removePermission(this.permission);
		}

		@Override
		public String toString()
		{
			return "permission " + this.permission.getEntered();
		}

		@Override
		public String observe(Permissible permissible)
		{
			return permissible.testPermission(this.permission.getNode()).name();
		}
	}

	final static class GroupAddable implements Addable
	{
		GroupAddable(Group group)
		{
			this.group = group;
		}

		private final Group group;

		@Override
		public boolean add(Permissible permissible)
		{
			return permissible.addGroup(this.group);
		}

		@Override
		public boolean remove(Permissible permissible)
		{
			return permissible.removeGroup(this.group);
		}

		@Override
		public String toString()
		{
			return "group " + this.group.getName();
		}

		@Override
		public String observe(Permissible permissible)
		{
			return String.valueOf(permissible.hasGroup(this.group));
		}
	}

	public static final class LimitAddable implements Addable
	{
		private static final Pattern PATTERN = Pattern.compile("(:|;)");
		public static LimitAddable valueOf(Permissible permissible, String context)
		{
			// homes:+10

			String[] split = LimitAddable.PATTERN.split(context, 2);

			if (split.length == 0) return null;

			String path = split[0];

			if (split.length == 1) return new LimitAddable(path, Limit.none());

			String value = split[1];
			int limitInt = 0;

			if (value.indexOf('+') == 0)
			{
				Limit currentLimit = permissible.getLimit(path);
				int currentLimitInt = currentLimit.toInt();

				if (currentLimitInt == -1) return new LimitAddable(path, currentLimit);

				limitInt = currentLimitInt;

				value = value.substring(1);
			}

			limitInt += NumberUtils.getInt(NumberUtils.parseInteger(value));

			return new LimitAddable(path, Limit.valueOf(limitInt));
		}

		LimitAddable(String path, Limit limit)
		{
			this.path = path;
			this.limit = limit;
		}

		private final String path;
		private final Limit limit;

		public String getPath()
		{
			return this.path;
		}

		public Limit getLimit()
		{
			return this.limit;
		}

		@Override
		public boolean add(Permissible permissible)
		{
			return permissible.setLimit(this.path, this.limit);
		}

		@Override
		public boolean remove(Permissible permissible)
		{
			return permissible.removeLimit(this.path);
		}

		@Override
		public String toString()
		{
			return "limit " + this.path + ' ' + this.limit.toString();
		}

		@Override
		public String observe(Permissible permissible)
		{
			return this.limit.isLargerThan(permissible.getLimit(this.path)) ? "smaller or equal to" : "bigger";
		}
	}

}