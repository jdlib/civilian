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
package org.civilian.processor;


import org.civilian.Application;
import org.civilian.asset.AssetService;
import org.civilian.request.Request;
import org.civilian.asset.Asset;
import org.civilian.resource.Path;
import org.civilian.response.Response;
import org.civilian.util.Check;
import org.civilian.util.HttpHeaders;


/**
 * AssetDispatch serves static resources, i.e. ({@link Asset Assets}).
 * It uses the {@link Application#getAssetService()} of the application to locate assets.
 * The AssetDispatch accepts the following request methods:
 * <ul>
 * <li>OPTIONS: returns the list of accepted methods
 * <li>GET, POST: returns the asset
 * <li>HEAD: returns the asset head information
 * </ul>
 */
public class AssetDispatch extends Processor
{
	/**
	 * Creates a new AssetDispatch.
	 * @param assetService the asset service used by the AssetDispatch.
	 */
	public AssetDispatch(AssetService assetService)
	{
		assetService_ = Check.notNull(assetService, "assetService");
		if (!assetService.hasAssets())
			throw new IllegalArgumentException("AssetService is empty");
	}
	

	@Override public String getInfo() 
	{
		return assetService_.getInfo();
	}

	
	/**
	 * Tries to find the asset corresponding to the request path.
	 * If not found, it invokes the next processor in the processor chain.
	 * Else it send the asset to the client.  
	 */
	@Override public boolean process(Request request, ProcessorChain chain) throws Exception
	{
		Path relativePath = request.getRelativePath();
		
		// catch if someone tries to sneak into private folders (e.g. WEB-INF)
		if (request.getServer().isProhibitedPath(relativePath.toString()))
		{
			request.getResponse().sendError(Response.Status.SC404_NOT_FOUND);
			return true;
		}
		
		// if this request does not match an asset, run the next processor in the chain 
		Asset asset = assetService_.getAsset(relativePath);
		if (asset == null)
			return chain.next(request); // not an asset request
		else
			return processAsset(request, asset);
	}
	
	
	protected boolean processAsset(Request request, Asset asset) throws Exception
	{
		Response response = request.getResponse();
		String method = request.getMethod();
		if (!VALID_METHODS.contains(method))
			response.sendError(Response.Status.SC405_METHOD_NOT_ALLOWED);
		else if ("OPTIONS".equals(method))
			response.getHeaders().set(HttpHeaders.ALLOW, VALID_METHODS);
		else
			asset.write(response, !"HEAD".equals(method) /*write content*/);
		return true; // we handled this request
	}

	
	private final AssetService assetService_;
	private static final String VALID_METHODS = "GET, HEAD, POST, OPTIONS";
}
