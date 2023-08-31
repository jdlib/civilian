/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.upload;


import org.civilian.request.Request;
import org.civilian.request.Upload;
import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.FormTableMixin;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;


public class IndexTemplate extends CspTemplate
{
	public IndexTemplate(IndexForm form)
	{
		this.form = form;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
		lang = new LangMixin(out);
		formTable = new FormTableMixin(out);
	}


	@Override protected void exit()
	{
		html = null;
		lang = null;
		formTable = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		Request request = form.getRequest();                            // line 7: @Request request = form.getRequest();
		out.println("<!DOCTYPE html>");                                 // line 8: <!DOCTYPE html>
		out.println("<html>");                                          // line 9: <html>
		out.println("<head>");                                          // line 10: <head>
		out.increaseTab();
		html.metaContentType();                                         // line 11: @html.metaContentType();
		html.linkCss("/css/lib/bootstrap.css");                         // line 12: @html.linkCss("/css/lib/bootstrap.css");
		html.linkCss("/css/samples.css");                               // line 13: @html.linkCss("/css/samples.css");
		out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"); // line 14: <meta http-equiv="X-UA-Compatible" content="IE=edge">
		out.println("<title>Civilian Upload Sample</title>");           // line 15: <title>Civilian Upload Sample</title>
		out.decreaseTab();
		out.println("</head>");                                         // line 16: </head>
		out.println("<body>");                                          // line 17: <body>
		out.println("<div class=\"container\">");                       // line 18: <div class="container">
		out.println("<div class=\"row\">");                             // line 19: <div class="row">
		out.increaseTab();
		out.println("<h1>Upload Sample</h1>");                          // line 20: <h1>Upload Sample</h1>
		out.println("<p class=\"doc\">");                               // line 21: <p class="doc">
		out.increaseTab();
		out.println("The upload sample demonstrates upload of files via"); // line 22: The upload sample demonstrates upload of files via
		out.println("<a target=\"_blank\" href=http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2\">multipart/form-data</a> requests.<br>"); // line 23: <a target="_blank" href=http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2">multipart/form-data</a> requests.<br>
		out.println("To allow upload requests, you need to configure your application accordingly via civilian.ini or Application#init(AppConfig).<br>"); // line 24: To allow upload requests, you need to configure your application accordingly via civilian.ini or Application#init(AppConfig).<br>
		out.println("To read uploaded files from a request use <code>request.getUpload(<i>parameter name</i>)</code>"); // line 25: To read uploaded files from a request use <code>request.getUpload(<i>parameter name</i>)</code>
		out.println("or <code>request.getUploads(<i>parameter name</i>)</code>.<br>"); // line 26: or <code>request.getUploads(<i>parameter name</i>)</code>.<br>
		out.println("The <code>FileField</code> class of the Civilian form library used in the sample is a convenient wrapper around this API."); // line 27: The <code>FileField</code> class of the Civilian form library used in the sample is a convenient wrapper around this API.
		out.decreaseTab();
		out.println("</p>");                                            // line 28: </p>
		out.println();
		out.println("<h3 style=\"margin-top:30px\">1. Select one or more files and click OK</h3>"); // line 30: <h3 style="margin-top:30px">1. Select one or more files and click OK</h3>
		out.println("<p>");                                             // line 31: <p>
		form.start(out);                                                // line 32: @form.start(out);
		out.println("<table class=\"table table-form\">");              // line 33: <table class="table table-form">
		out.increaseTab();
		formTable.row(form.file);                                       // line 34: @formTable.row(form.file);
		formTable.row(form.ok);                                         // line 35: @formTable.row(form.ok);
		out.decreaseTab();
		out.println("</table>");                                        // line 36: </table>
		form.end(out);                                                  // line 37: @form.end(out);
		Exception uploadError = request.getUploads().error();           // line 38: @Exception uploadError = request.getUploads().error();
		if (uploadError != null)                                        // line 39: @if (uploadError != null)
		{
			out.println("<p>");                                         // line 40: <p>
			out.println("<h3>2. The server received an error:</h3>");   // line 41: <h3>2. The server received an error:</h3>
			out.println("<code>request.getUploads().error()</code> says<p><br>"); // line 42: <code>request.getUploads().error()</code> says<p><br>
			html.stackTrace(uploadError);                               // line 43: <%html.stackTrace(uploadError);%>
			out.printlnIfNotEmpty();
		}
		else                                                            // line 44: @else
		{
			Upload[] uploads = form.file.getUploads();                  // line 45: @Upload[] uploads = form.file.getUploads();
			if (uploads.length != 0)                                    // line 46: @if (uploads.length != 0)
			{
				out.println("<p>");                                     // line 47: <p>
				out.print("<h3 style=\"margin-top:30px\">2. The server received "); // line 48: <h3 style="margin-top:30px">2. The server received
				out.print(uploads.length);                              // line 48: <%uploads.length%>
				out.println(" uploads</h3>");                           // line 48: uploads</h3>
				out.println("<table class=\"table table-bordered table-form\">"); // line 49: <table class="table table-bordered table-form">
				out.println("<tr>");                                    // line 50: <tr>
				out.increaseTab();
				out.println("<th>#</th>");                              // line 51: <th>#</th>
				out.println("<th>upload.length()</th>");                // line 52: <th>upload.length()</th>
				out.println("<th>upload.getContentType()</th>");        // line 53: <th>upload.getContentType()</th>
				out.println("<th>upload.getFileName()</th>");           // line 54: <th>upload.getFileName()</th>
				out.decreaseTab();
				out.println("</tr>");                                   // line 55: </tr>
				for (int i=0; i<uploads.length; i++)                    // line 56: @for (int i=0; i<uploads.length; i++)
				{
					Upload upload = uploads[i];                         // line 57: @Upload upload = uploads[i];
					out.println("<tr>");                                // line 58: <tr>
					out.increaseTab();
					out.print("<td>");                                  // line 59: <td>
					out.print(i);                                       // line 59: <%i%>
					out.println("</td>");                               // line 59: </td>
					out.print("<td>");                                  // line 60: <td>
					out.print(lang.format(upload.length()));            // line 60: <%lang.format(upload.length())%>
					out.println(" bytes</td>");                         // line 60: bytes</td>
					out.print("<td>");                                  // line 61: <td>
					out.print(upload.getContentType());                 // line 61: <%upload.getContentType()%>
					out.println("</td>");                               // line 61: </td>
					out.print("<td>");                                  // line 62: <td>
					out.print(upload.getFileName());                    // line 62: <%upload.getFileName()%>
					out.println("</td>");                               // line 62: </td>
					out.decreaseTab();
					out.println("</tr>");                               // line 63: </tr>
				}
				out.println("</table>");                                // line 64: </table>
				out.println("</div>");                                  // line 65: </div>
			}
			else if (request.hasMethod("POST"))                         // line 66: @else if (request.hasMethod("POST"))
			{
				out.println("Did not receive any uploads... :-(<br>");  // line 67: Did not receive any uploads... :-(<br>
			}
		}
		out.decreaseTab();
		out.println("</div>");                                          // line 68: </div>
		out.println("</div>");                                          // line 69: </div>
		out.println("</body>");                                         // line 70: </body>
		out.println("</html>");                                         // line 71: </html>
	}


	protected IndexForm form;
	protected HtmlMixin html;
	protected LangMixin lang;
	protected FormTableMixin formTable;
}
