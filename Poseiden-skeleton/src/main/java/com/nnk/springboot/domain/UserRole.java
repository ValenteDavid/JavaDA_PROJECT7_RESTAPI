package com.nnk.springboot.domain;

public class UserRole {

	public enum typeUser {

		USER("USER",false, true, false, false),
		ADMIN("ADMIN", true, true, true, true);

		private String name;
		private boolean create;
		private boolean read;
		private boolean update;
		private boolean delete;

		private typeUser(String name,boolean create, boolean read, boolean update, boolean delete) {
			this.name=name;
			this.create = create;
			this.read = read;
			this.update = update;
			this.delete = delete;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean create() {
			return create;
		}

		public boolean read() {
			return read;
		}

		public boolean update() {
			return update;
		}

		public boolean delete() {
			return delete;
		}
		
	}

}
