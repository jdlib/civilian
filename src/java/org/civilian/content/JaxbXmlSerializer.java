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
package org.civilian.content;


import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.civilian.application.AppConfig;
import org.civilian.application.Application;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * JaxbXmlReader is a ContentSerializer implementation for 
 * XML documents which uses JAXB for serialization. 
 * If you want to use a JaxbXmlReader, then
 * during application setup create an appropriate JAXBContent,
 * create a JaxbXmlSerializer with that context, and add it
 * to the serializers of the application.
 * @see AppConfig#getContentSerializers()
 * @see Application#init(AppConfig) 
 */
public class JaxbXmlSerializer extends ContentSerializer
{
	/**
	 * Creates a new JaxbXmlReader.
	 * @param context the JAXBContext used by the JaxbXmlReader
	 */
	public JaxbXmlSerializer(JAXBContext context)
	{
		context_ = Check.notNull(context, "context");
	}
	
	
	/**
	 * Unmarshalls the request content.
	 */
	@SuppressWarnings("unchecked")
	@Override public <T> T read(Class<T> type, Type genericType, Reader reader) throws Exception
	{
		Check.notNull(type, "type");
		Check.notNull(reader, "reader");
		
		Object result = context_.createUnmarshaller().unmarshal(reader);
		if ((result != null) && !ClassUtil.isA(result, type))
			throw new IllegalArgumentException("expected a " + type.getName() + " object, but was a " + result.getClass().getName());
		return (T)result;
	}


	/**
	 * Writes the value to the response. 
	 */
	@Override public void write(Object value, Writer writer) throws Exception
	{
		context_.createMarshaller().marshal(value, writer);
	}
	

	/**
	 * Returns the JAXBContext if implClass equals JAXBContext.class.
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return ClassUtil.unwrap(context_, implClass);
	}

	
	/**
	 * Returns the exception message if the exception is a JAXBException, else null.
	 */
	@Override public String describeReadError(Exception e)
	{
		return e instanceof JAXBException ? e.getMessage() : null;
	}
	
	
	private final JAXBContext context_;
}
