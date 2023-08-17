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


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.civilian.util.Check;


/**
 * UrlAsset is an Asset implementation,
 * which uses a URL to references its content. 
 */
public class UrlAsset extends AbstractAsset
{
	public UrlAsset(URL url)
	{
		url_ = Check.notNull(url, "url");
	}
	

	@Override public InputStream getInputStream() throws IOException
	{
		return url_.openStream();
	}
	
	
	@Override public boolean isValid()
	{
		return true;
	}
	
	
	@Override public String toString()
	{
		return url_.toString();
	}

	
	private final URL url_;
}
