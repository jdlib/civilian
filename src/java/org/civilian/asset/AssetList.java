/*
 * Copyright (C) 2014 Civilian Framework.
 *
 * Licensed under the Civilian License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.civilian-framework.org/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.civilian.asset;


import org.civilian.template.mixin.HtmlMixin;
import org.civilian.util.Check;
import org.civilian.provider.ApplicationProvider;
import org.civilian.template.TemplateWriter;


/**
 * AssetList stores a list a of asset paths. 
 * All assets must have same type, e.g. CSS or Javascript.
 * The AssetList itself can be printed in a HTML template:
 * Depending on its type, it will print appropriate 
 * CSS links or Javascript script elements for its assets.<br>
 * Optionally you can also specify a production asset,
 * which - in case the application is running in production mode -
 * will be printed instead of the whole list of assets.
 */
public class AssetList implements TemplateWriter.Printable
{
	/**
	 * A Type implementation for CSS assets.
	 */
	public static final Type CSS_TYPE = new CssType();
	

	/**
	 * A Type implementation for Javascript assets.
	 */
	public static final Type JS_TYPE  = new JsType();

	
	/**
	 * Creates a new AssetList.
	 * @param type the type of the list.
	 * @param paths the paths of assets, relative to the asset root. 
	 */
	public AssetList(Type type, String... paths)
	{
		type_	= Check.notNull(type, "type");
		paths_	= Check.notEmpty(paths, "paths");
	}

	
	/**
	 * Returns the number of asset paths stored in the list.
	 */
	public int size()
	{
		return paths_.length;
	}
	
	
	/**
	 * Returns the path of the i-th asset.
	 */
	public String getPath(int i)
	{
		return paths_[i];
	}
	
	
	/**
	 * Returns the type of the asset list.
	 */
	public Type getType()
	{
		return type_;
	}
	
	
	/**
	 * Sets the paths of the production items. If the paths are
	 * not null and not empty and if the application is running in production mode,
	 * the production paths will be printed instead of the whole list of assets path.
	 */
	public AssetList setProductionPaths(String... paths)
	{
		if ((paths != null) && (paths.length == 0))
			paths = null;
		productionPaths_ = paths;
		return this;
	}

	
	/**
	 * Returns the paths of the production items.
	 */
	public String[] getProductionPaths()
	{
		return productionPaths_;
	}

	
	/**
	 * Prints the asset list to the TemplateWriter.
	 */
	@Override public void print(TemplateWriter out) throws Exception
	{
		HtmlMixin html = new HtmlMixin(out);
		String[] paths = (productionPaths_ != null) && !out.getSafeContext(ApplicationProvider.class).getApplication().develop() ?
			productionPaths_ :
			paths_;
		for (String path : paths)
			type_.printRef(path, html);
	}
	
	
	/**
	 * Type defines what type of assets are stored
	 * in a AssetList.
	 */
	public static interface Type
	{
		/**
		 * Called for every item in the AssetList.
		 * It should print HTML content to include the item, 
		 * e.g. a css link element or script element.
		 * @param path the path to the item of the asset list
		 */
		public void printRef(String path, HtmlMixin html);
	}
	

	private static class CssType implements Type
	{
		@Override public void printRef(String path, HtmlMixin html)
		{
			html.linkCss(path);
		}
	}


	private static class JsType implements Type
	{
		@Override public void printRef(String path, HtmlMixin html)
		{
			html.script(path);
		}
	}
	

	private Type type_;
	private String[] productionPaths_;
	private String[] paths_;
}
