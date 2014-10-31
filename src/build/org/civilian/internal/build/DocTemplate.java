/**
 * Generated from DocTemplate.csp
 * Do not edit.
 */
package org.civilian.internal.build;


import java.util.List;
import org.civilian.util.StringUtil;
import org.civilian.util.TabWriter;


public class DocTemplate
{
	public DocTemplate(String inputFile, List<String> lines)
	{
		this.inputFile = inputFile;
		this.lines = lines;
	}


	public synchronized void print(TabWriter out)
	{
		this.out = out;
		print();
	}


	protected void print()
	{
		out.println("<!DOCTYPE html>");                                 // line 7: <!DOCTYPE html>
		out.println("<html>");                                          // line 8: <html>
		out.println("<head>");                                          // line 9: <head>
		out.increaseTab();
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\">"); // line 10: <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"); // line 11: <meta http-equiv="X-UA-Compatible" content="IE=edge">
		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"); // line 12: <meta name="viewport" content="width=device-width, initial-scale=1">
		out.println("<meta name=\"description\" content=\"civilian java web framework\">"); // line 13: <meta name="description" content="civilian java web framework">
		out.print("<title>");                                           // line 14: <title>
		printTitle();                                                   // line 14: <%printTitle();%>
		out.println("</title>");                                        // line 14: </title>
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"res/bootstrap/css/bootstrap.min.css\">"); // line 15: <link rel="stylesheet" type="text/css" href="res/bootstrap/css/bootstrap.min.css">
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"res/civilian.css\">"); // line 16: <link rel="stylesheet" type="text/css" href="res/civilian.css">
		out.decreaseTab();
		out.println("</head>");                                         // line 17: </head>
		out.println("<body>");                                          // line 18: <body>
		out.println("<nav class=\"navbar navbar-default navbar-fixed-top hidden-print\" role=\"navigation\">"); // line 19: <nav class="navbar navbar-default navbar-fixed-top hidden-print" role="navigation">
		out.increaseTab();
		out.println("<div class=\"container\">");                       // line 20: <div class="container">
		out.increaseTab();
		out.println("<div class=\"navbar-header\">");                   // line 21: <div class="navbar-header">
		out.increaseTab();
		out.println("<button class=\"navbar-toggle\" data-target=\".navbar-collapse\" data-toggle=\"collapse\" type=\"button\">"); // line 22: <button class="navbar-toggle" data-target=".navbar-collapse" data-toggle="collapse" type="button">
		out.increaseTab();
		out.println("<span class=\"sr-only\">Toggle navigation</span>"); // line 23: <span class="sr-only">Toggle navigation</span>
		out.println("<span class=\"icon-bar\"></span>");                // line 24: <span class="icon-bar"></span>
		out.println("<span class=\"icon-bar\"></span>");                // line 25: <span class="icon-bar"></span>
		out.println("<span class=\"icon-bar\"></span>");                // line 26: <span class="icon-bar"></span>
		out.decreaseTab();
		out.println("</button>");                                       // line 27: </button>
		out.println("<!--<a class=\"navbar-brand civilian civ-brand\" href=\"index.html\">CIVILIAN</a>  -->"); // line 28: <!--<a class="navbar-brand civilian civ-brand" href="index.html">CIVILIAN</a>  -->
		out.decreaseTab();
		out.println("</div>");                                          // line 29: </div>
		out.println("<div class=\"navbar-collapse collapse\">");        // line 30: <div class="navbar-collapse collapse">
		out.increaseTab();
		out.println("<ul class=\"nav navbar-nav\">");                   // line 31: <ul class="nav navbar-nav">
		out.increaseTab();
		printNav("index", 		"Home");                              // line 32: @printNav("index", 		"Home");
		printNav("intro", 		"Introduction");                      // line 33: @printNav("intro", 		"Introduction");
		printNav("quickstart", "Quickstart");                           // line 34: @printNav("quickstart", "Quickstart");
		printNav("doc", 		"Documentation");                       // line 35: @printNav("doc", 		"Documentation");
		printNav("download", 	"Download");                           // line 36: @printNav("download", 	"Download");
		printNav("license", 	"License");                             // line 37: @printNav("license", 	"License");
		out.decreaseTab();
		out.println("</ul>");                                           // line 38: </ul>
		out.println("<ul class=\"nav navbar-nav navbar-right\">");      // line 39: <ul class="nav navbar-nav navbar-right">
		out.increaseTab();
		printNav("contact",	"Contact/Imprint");                      // line 40: @printNav("contact",	"Contact/Imprint");
		out.decreaseTab();
		out.println("</ul>");                                           // line 41: </ul>
		out.decreaseTab();
		out.println("</div>");                                          // line 42: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 43: </div>
		out.decreaseTab();
		out.println("</nav>");                                          // line 44: </nav>
		out.println("<div class=\"container\">");                       // line 45: <div class="container">
		for (String line : lines)                                       // line 46: @for (String line : lines)
		{
			out.print(line);                                            // line 47: <%line%>
			out.printlnIfNotEmpty();
		}
		if (!inputFile.startsWith("index"))                             // line 48: @if (!inputFile.startsWith("index"))
		{
			out.println("<div class=\"row\">");                         // line 49: <div class="row">
			out.increaseTab();
			out.println("<div class=\"col-md-3\"></div>");              // line 50: <div class="col-md-3"></div>
			out.println("<div class=\"col-md-9\">");                    // line 51: <div class="col-md-9">
			out.increaseTab();
			out.println("<hr>");                                        // line 52: <hr>
			out.println("<p class=\"footer\">Copyright &copy; 2014. All rights reserved.</p>"); // line 53: <p class="footer">Copyright &copy; 2014. All rights reserved.</p>
			out.decreaseTab();
			out.println("</div>");                                      // line 54: </div>
			out.decreaseTab();
			out.println("</div>");                                      // line 55: </div>
		}
		out.println("</div>");                                          // line 56: </div>
		out.println("</body>");                                         // line 57: </body>
		out.println("</html>");                                         // line 58: </html>
	}
	
	
	private void printNav(String file, String text)
	{
		out.print("<li");                                               // line 64: <li
		if (inputFile.startsWith(file))                                 // line 64: <%?inputFile.startsWith(file)%>
		{
			out.print(" class=\"active\"");                             // line 64: class="active"
		}
		out.print("><a href=\"");                                       // line 64: ><a href="
		out.print(file);                                                // line 64: <%file%>
		out.print(".html\">");                                          // line 64: .html">
		out.print(text);                                                // line 64: <%text%>
		out.println("</a></li>");                                       // line 64: </a></li>
	}
	
	
	private void printTitle()
	{
		out.print("Civilian");                                          // line 70: Civilian
		String file = StringUtil.cutRight(inputFile, ".html");          // line 71: @String file = StringUtil.cutRight(inputFile, ".html");
		if (!"index".equals(file))                                      // line 72: @if (!"index".equals(file))
		{
			for (String part : file.split("-"))                         // line 73: @for (String part : file.split("-"))
			{
				out.print(" - ");                                       // line 74: -
				out.print(StringUtil.startUpperCase(part));             // line 74: <%StringUtil.startUpperCase(part)%>
			}
		}
	}


	private String inputFile;
	private List<String> lines;
	protected TabWriter out;
}
