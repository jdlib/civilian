/**
 * Generated from GameTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.game;


import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.FormTableMixin;
import org.civilian.template.mixin.HtmlMixin;


public class GameTemplate extends CspTemplate
{
	public GameTemplate(Game game, GameForm form, String feedback)
	{
		this.game = game;
		this.form = form;
		this.feedback = feedback;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
		formTable = new FormTableMixin(out);
	}


	@Override protected void exit()
	{
		html = null;
		formTable = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 3: <!DOCTYPE html>
		out.println("<html>");                                          // line 4: <html>
		out.println("<head>");                                          // line 5: <head>
		out.increaseTab();
		html.metaContentType();                                         // line 6: @html.metaContentType();
		html.linkCss("css/lib/bootstrap.css");                          // line 7: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/samples.css");                                // line 8: @html.linkCss("css/samples.css");
		out.println("<title>Civilian Game Sample</title>");             // line 9: <title>Civilian Game Sample</title>
		out.decreaseTab();
		out.println("</head>");                                         // line 10: </head>
		out.println("<body>");                                          // line 11: <body>
		out.increaseTab();
		out.println("<h1>Guess a number:</h1>");                        // line 12: <h1>Guess a number:</h1>
		out.print("I'm thinking of a number between ");                 // line 13: I'm thinking of a number between
		out.print(game.getSmallest());                                  // line 13: <%game.getSmallest()%>
		out.print(" and ");                                             // line 13: and
		out.print(game.getBiggest());                                   // line 13: <%game.getBiggest()%>
		out.println(".<br>");                                           // line 13: .<br>
		out.print("You have ");                                         // line 14: You have
		out.print(game.getRemainingGuesses());                          // line 14: <%game.getRemainingGuesses()%>
		out.println(" guesses remaining.");                             // line 14: guesses remaining.
		out.println("<p>");                                             // line 15: <p>
		formTable.print(form);                                          // line 16: @formTable.print(form);
		if (feedback != null)                                           // line 17: @if (feedback != null)
		{
			out.print("<div>");                                         // line 18: <div>
			html.text(feedback);                                        // line 18: <%html.text(feedback);%>
			out.println("</div>");                                      // line 18: </div>
		}
		out.decreaseTab();
		out.println("</body>");                                         // line 19: </body>
		out.println("</html>");                                         // line 20: </html>
	}


	protected Game game;
	protected GameForm form;
	protected String feedback;
	protected HtmlMixin html;
	protected FormTableMixin formTable;
}
