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
package org.civilian.server.servlet;


import jakarta.servlet.http.Part;
import org.civilian.util.http.HeaderParser;


/**
 * Helper class to handle a Part object of a Servlet request.
 */
class PartInfo
{
	public PartInfo(Part part)
	{
		String contentDisposition = part.getHeader("Content-Disposition");
		if (contentDisposition != null)
			parseContentDisposition(contentDisposition);
	}
	
	
	private void parseContentDisposition(String contentDisposition)
	{
		HeaderParser parser = new HeaderParser(contentDisposition);
		if ((parser.next() == HeaderParser.Token.ITEM) && 
			parser.hasItemIgnoreCase("form-data"))
		{
			isFormData = true;
			while(parser.next() == HeaderParser.Token.PARAM)
			{
				if (parser.hasParamIgnoreCase("filename"))
				{
					this.origFileName = parser.paramValue;
					int slash = Math.max(origFileName.lastIndexOf('/'), origFileName.lastIndexOf('\\'));
					this.fileName = slash >= 0 ?  this.origFileName.substring(slash + 1) : this.origFileName;
				}
			}
		}
	}

	
	boolean isFormData = false;
	String fileName    = null;
	String origFileName= null;
}
