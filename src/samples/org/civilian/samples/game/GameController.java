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
 package org.civilian.samples.game;


import org.civilian.Controller;
import org.civilian.annotation.Get;
import org.civilian.annotation.Path;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.samples.game.Game.Result;


/**
 * See https://github.com/wildfly/quickstart/blob/master/guide/NumberguessQuickstart.asciidoc
 */
@Path("/")
public class GameController extends Controller
{
	@Get @Post @Produces("text/html") 
	public void render() throws Exception
	{
		Game game 		= getRequest().getSession(true).getCreateAttr(Game.class);
		GameForm form 	= new GameForm(this);
		String feedback = null;
		
		if (form.reset.isClicked())
			game.reset();
		else if (form.ok.isClicked() && form.read())
		{
			int guess = form.guess.getIntValue();
			Game.Result result = game.play(guess);
			feedback = result.toString();
			if (result != Result.correct)
				form.guess.setValue(null);
		}
		
		form.guess.setMin(game.getSmallest());
		form.guess.setMax(game.getBiggest());
		
		GameTemplate t = new GameTemplate(game, form, feedback);
		getResponse().writeTemplate(t);
	}
}
