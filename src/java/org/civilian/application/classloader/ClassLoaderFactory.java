package org.civilian.application.classloader;


import org.civilian.util.Check;


public interface ClassLoaderFactory 
{
	public boolean isReloading();


	public ClassLoader getAppClassLoader();


	public ClassLoader getRequestClassLoader();


	public static class Dev implements ClassLoaderFactory 
	{
		public Dev(ClassLoader appClassLoader, ReloadConfig reloadConfig)
		{
			appClassLoader_ = Check.notNull(appClassLoader, "appClassLoader");
			reloadConfig_   = Check.notNull(reloadConfig, "reloadConfig");
		}
		
		
		@Override public boolean isReloading() 
		{
			return true;
		}


		@Override public ClassLoader getAppClassLoader()
		{
			return appClassLoader_;
		}


		@Override public ClassLoader getRequestClassLoader() 
		{
			return new DevRequestClassLoader(appClassLoader_, reloadConfig_);
		}


		private final ClassLoader appClassLoader_;
		private final ReloadConfig reloadConfig_;
	}


	public static class Production implements ClassLoaderFactory 
	{
		public Production()
		{
			appClassLoader_ = Production.class.getClassLoader();
		}

		
		public Production(ClassLoader appClassLoader)
		{
			appClassLoader_ = Check.notNull(appClassLoader, "appClassLoader");
		}
		
		
		@Override public boolean isReloading() 
		{
			return false;
		}


		@Override public ClassLoader getAppClassLoader()
		{
			return appClassLoader_;
		}

	 
		@Override public ClassLoader getRequestClassLoader() 
		{
			return appClassLoader_; // same as app class loader
		}

	
		private final ClassLoader appClassLoader_;
	}
}


