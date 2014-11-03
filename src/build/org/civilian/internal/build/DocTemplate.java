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
		try
		{
			this.out = out;
			print();
		}
		finally
		{
			this.out = null;
		}
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
		out.println("<ul class=\"nav navbar-nav\">");                   // line 21: <ul class="nav navbar-nav">
		out.increaseTab();
		printNav("index", 		"Home");                              // line 22: @printNav("index", 		"Home");
		printNav("intro", 		"Introduction");                      // line 23: @printNav("intro", 		"Introduction");
		printNav("quickstart", "Quickstart");                           // line 24: @printNav("quickstart", "Quickstart");
		printNav("doc", 		"Documentation");                       // line 25: @printNav("doc", 		"Documentation");
		printNav("download", 	"Download");                           // line 26: @printNav("download", 	"Download");
		printNav("license", 	"License");                             // line 27: @printNav("license", 	"License");
		out.decreaseTab();
		out.println("</ul>");                                           // line 28: </ul>
		out.println("<ul class=\"nav navbar-nav navbar-right\">");      // line 29: <ul class="nav navbar-nav navbar-right">
		out.increaseTab();
		printNav("contact",	"Contact/Imprint");                      // line 30: @printNav("contact",	"Contact/Imprint");
		out.decreaseTab();
		out.println("</ul>");                                           // line 31: </ul>
		out.decreaseTab();
		out.println("</div>");                                          // line 32: </div>
		out.decreaseTab();
		out.println("</nav>");                                          // line 33: </nav>
		out.println("<div class=\"container\">");                       // line 34: <div class="container">
		for (String line : lines)                                       // line 35: @for (String line : lines)
		{
			out.print(line);                                            // line 36: <%line%>
			out.printlnIfNotEmpty();
		}
		if (!inputFile.startsWith("index"))                             // line 37: @if (!inputFile.startsWith("index"))
		{
			out.println("<div class=\"row\">");                         // line 38: <div class="row">
			out.increaseTab();
			out.println("<div class=\"col-md-3\"></div>");              // line 39: <div class="col-md-3"></div>
			out.println("<div class=\"col-md-9\">");                    // line 40: <div class="col-md-9">
			out.increaseTab();
			out.println("<hr>");                                        // line 41: <hr>
			out.println("<p class=\"footer\">Copyright &copy; 2014. All rights reserved.</p>"); // line 42: <p class="footer">Copyright &copy; 2014. All rights reserved.</p>
			out.decreaseTab();
			out.println("</div>");                                      // line 43: </div>
			out.decreaseTab();
			out.println("</div>");                                      // line 44: </div>
		}
		out.println("</div>");                                          // line 45: </div>
		out.println("</body>");                                         // line 46: </body>
		out.println("</html>");                                         // line 47: </html>
	}
	
	
	private void printNav(String file, String text)
	{
		out.print("<li");                                               // line 53: <li
		if (inputFile.startsWith(file))                                 // line 53: <%?inputFile.startsWith(file)%>
		{
			out.print(" class=\"active\"");                             // line 53: class="active"
		}
		out.print("><a href=\"");                                       // line 53: ><a href="
		out.print(file);                                                // line 53: <%file%>
		out.print(".html\">");                                          // line 53: .html">
		out.print(text);                                                // line 53: <%text%>
		out.println("</a></li>");                                       // line 53: </a></li>
	}
	
	
	private void printTitle()
	{
		out.print("Civilian");                                          // line 59: Civilian
		String file = StringUtil.cutRight(inputFile, ".html");          // line 60: @String file = StringUtil.cutRight(inputFile, ".html");
		if (!"index".equals(file))                                      // line 61: @if (!"index".equals(file))
		{
			for (String part : file.split("-"))                         // line 62: @for (String part : file.split("-"))
			{
				out.print(" - ");                                       // line 63: -
				out.print(StringUtil.startUpperCase(part));             // line 63: <%StringUtil.startUpperCase(part)%>
			}
		}
	}


	private String inputFile;
	private List<String> lines;
	protected TabWriter out;
}
