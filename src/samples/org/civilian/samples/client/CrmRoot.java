/**
 * Generated by org.civilian.tool.resource.ClientConstGenerator.
 * Do not edit.
 */
package org.civilian.samples.client;


/**
 * Resource "/" of application org.civilian.samples.crm.web.CrmApp.
 */
public class CrmRoot extends org.civilian.client.WebResource
{
	public CrmRoot(String url)
	{
		super(url);

		addChild(this.contacts = new Contacts(this));
		addChild(this.customers = new Customers(this));
		addChild(this.login = new org.civilian.client.WebResource(this, "login"));
		addChild(this.logout = new org.civilian.client.WebResource(this, "logout"));
		addChild(this.opportunities = new Opportunities(this));
		addChild(this.users = new Users(this));
	}

	/**
	 * Resource "/contacts".
	 */
	public final Contacts contacts;

	/**
	 * Resource "/customers".
	 */
	public final Customers customers;

	/**
	 * Resource "/login".
	 */
	public final org.civilian.client.WebResource login;

	/**
	 * Resource "/logout".
	 */
	public final org.civilian.client.WebResource logout;

	/**
	 * Resource "/opportunities".
	 */
	public final Opportunities opportunities;

	/**
	 * Resource "/users".
	 */
	public final Users users;


	/**
	 * Resource "/contacts".
	 */
	public static class Contacts extends org.civilian.client.WebResource
	{
		public Contacts(org.civilian.client.WebResource parent)
		{
			super(parent, "contacts");

			addChild(this.search = new Search(this));
			addChild(this.$contactId = new org.civilian.client.WebResource(this, org.civilian.samples.crm.web.CrmPathParams.CONTACTID));
		}

		/**
		 * Resource "/contacts/search".
		 */
		public final Search search;

		/**
		 * Resource "/contacts/{contactId}".
		 */
		public final org.civilian.client.WebResource $contactId;


		/**
		 * Resource "/contacts/search".
		 */
		public static class Search extends org.civilian.client.WebResource
		{
			public Search(org.civilian.client.WebResource parent)
			{
				super(parent, "search");

				addChild(this.filter = new org.civilian.client.WebResource(this, "filter"));
			}

			/**
			 * Resource "/contacts/search/filter".
			 */
			public final org.civilian.client.WebResource filter;
		}
	}


	/**
	 * Resource "/customers".
	 */
	public static class Customers extends org.civilian.client.WebResource
	{
		public Customers(org.civilian.client.WebResource parent)
		{
			super(parent, "customers");

			addChild(this.lookup = new org.civilian.client.WebResource(this, "lookup"));
			addChild(this.navigation = new org.civilian.client.WebResource(this, "navigation"));
			addChild(this.search = new Search(this));
			addChild(this.$customerId = new $CustomerId(this));
		}

		/**
		 * Resource "/customers/lookup".
		 */
		public final org.civilian.client.WebResource lookup;

		/**
		 * Resource "/customers/navigation".
		 */
		public final org.civilian.client.WebResource navigation;

		/**
		 * Resource "/customers/search".
		 */
		public final Search search;

		/**
		 * Resource "/customers/{customerId}".
		 */
		public final $CustomerId $customerId;


		/**
		 * Resource "/customers/search".
		 */
		public static class Search extends org.civilian.client.WebResource
		{
			public Search(org.civilian.client.WebResource parent)
			{
				super(parent, "search");

				addChild(this.filter = new org.civilian.client.WebResource(this, "filter"));
			}

			/**
			 * Resource "/customers/search/filter".
			 */
			public final org.civilian.client.WebResource filter;
		}


		/**
		 * Resource "/customers/{customerId}".
		 */
		public static class $CustomerId extends org.civilian.client.WebResource
		{
			public $CustomerId(org.civilian.client.WebResource parent)
			{
				super(parent, org.civilian.samples.crm.web.CrmPathParams.CUSTOMERID);

				addChild(this.details = new org.civilian.client.WebResource(this, "details"));
				addChild(this.masterdata = new org.civilian.client.WebResource(this, "masterdata"));
			}

			/**
			 * Resource "/customers/{customerId}/details".
			 */
			public final org.civilian.client.WebResource details;

			/**
			 * Resource "/customers/{customerId}/masterdata".
			 */
			public final org.civilian.client.WebResource masterdata;
		}
	}


	/**
	 * Resource "/opportunities".
	 */
	public static class Opportunities extends org.civilian.client.WebResource
	{
		public Opportunities(org.civilian.client.WebResource parent)
		{
			super(parent, "opportunities");

			addChild(this.search = new Search(this));
			addChild(this.$opportunityId = new org.civilian.client.WebResource(this, org.civilian.samples.crm.web.CrmPathParams.OPPORTUNITYID));
		}

		/**
		 * Resource "/opportunities/search".
		 */
		public final Search search;

		/**
		 * Resource "/opportunities/{opportunityId}".
		 */
		public final org.civilian.client.WebResource $opportunityId;


		/**
		 * Resource "/opportunities/search".
		 */
		public static class Search extends org.civilian.client.WebResource
		{
			public Search(org.civilian.client.WebResource parent)
			{
				super(parent, "search");

				addChild(this.filter = new org.civilian.client.WebResource(this, "filter"));
			}

			/**
			 * Resource "/opportunities/search/filter".
			 */
			public final org.civilian.client.WebResource filter;
		}
	}


	/**
	 * Resource "/users".
	 */
	public static class Users extends org.civilian.client.WebResource
	{
		public Users(org.civilian.client.WebResource parent)
		{
			super(parent, "users");

			addChild(this.search = new Search(this));
			addChild(this.$userId = new org.civilian.client.WebResource(this, org.civilian.samples.crm.web.CrmPathParams.USERID));
		}

		/**
		 * Resource "/users/search".
		 */
		public final Search search;

		/**
		 * Resource "/users/{userId}".
		 */
		public final org.civilian.client.WebResource $userId;


		/**
		 * Resource "/users/search".
		 */
		public static class Search extends org.civilian.client.WebResource
		{
			public Search(org.civilian.client.WebResource parent)
			{
				super(parent, "search");

				addChild(this.filter = new org.civilian.client.WebResource(this, "filter"));
			}

			/**
			 * Resource "/users/search/filter".
			 */
			public final org.civilian.client.WebResource filter;
		}
	}
}
