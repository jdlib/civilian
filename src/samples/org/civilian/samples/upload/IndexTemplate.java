/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.upload;


import org.civilian.request.Request;
import org.civilian.request.Upload;
import org.civilian.template.Template;
import org.civilian.template.mixin.FormTableMixin;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;


public class IndexTemplate extends Template
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
		if (request.getUploadError() != null)                           // line 38: @if (request.getUploadError() != null)
		{
			out.println("<p>");                                         // line 39: <p>
			out.println("<h3>2. The server received an error:</h3>");   // line 40: <h3>2. The server received an error:</h3>
			out.println("<code>request.getUploadError()</code> says<p><br>"); // line 41: <code>request.getUploadError()</code> says<p><br>
			html.stackTrace(request.getUploadError());                  // line 42: <%html.stackTrace(request.getUploadError());%>
			out.printlnIfNotEmpty();
		}
		else                                                            // line 43: @else
		{
			Upload[] uploads = form.file.getUploads();                  // line 44: @Upload[] uploads = form.file.getUploads();
			if (uploads.length != 0)                                    // line 45: @if (uploads.length != 0)
			{
				out.println("<p>");                                     // line 46: <p>
				out.print("<h3 style=\"margin-top:30px\">2. The server received "); // line 47: <h3 style="margin-top:30px">2. The server received
				out.print(uploads.length);                              // line 47: <%uploads.length%>
				out.println(" uploads</h3>");                           // line 47: uploads</h3>
				out.println("<table class=\"table table-bordered table-form\">"); // line 48: <table class="table table-bordered table-form">
				out.println("<tr>");                                    // line 49: <tr>
				out.increaseTab();
				out.println("<th>#</th>");                              // line 50: <th>#</th>
				out.println("<th>upload.length()</th>");                // line 51: <th>upload.length()</th>
				out.println("<th>upload.getContentType()</th>");        // line 52: <th>upload.getContentType()</th>
				out.println("<th>upload.getFileName()</th>");           // line 53: <th>upload.getFileName()</th>
				out.decreaseTab();
				out.println("</tr>");                                   // line 54: </tr>
				for (int i=0; i<uploads.length; i++)                    // line 55: @for (int i=0; i<uploads.length; i++)
				{
					Upload upload = uploads[i];                         // line 56: @Upload upload = uploads[i];
					out.println("<tr>");                                // line 57: <tr>
					out.increaseTab();
					out.print("<td>");                                  // line 58: <td>
					out.print(i);                                       // line 58: <%i%>
					out.println("</td>");                               // line 58: </td>
					out.print("<td>");                                  // line 59: <td>
					out.print(lang.format(upload.length()));            // line 59: <%lang.format(upload.length())%>
					out.println(" bytes</td>");                         // line 59: bytes</td>
					out.print("<td>");                                  // line 60: <td>
					out.print(upload.getContentType());                 // line 60: <%upload.getContentType()%>
					out.println("</td>");                               // line 60: </td>
					out.print("<td>");                                  // line 61: <td>
					out.print(upload.getFileName());                    // line 61: <%upload.getFileName()%>
					out.println("</td>");                               // line 61: </td>
					out.decreaseTab();
					out.println("</tr>");                               // line 62: </tr>
				}
				out.println("</table>");                                // line 63: </table>
				out.println("</div>");                                  // line 64: </div>
			}
			else if (request.hasMethod("POST"))                         // line 65: @else if (request.hasMethod("POST"))
			{
				out.println("Did not receive any uploads... :-(<br>");  // line 66: Did not receive any uploads... :-(<br>
			}
		}
		out.decreaseTab();
		out.println("</div>");                                          // line 67: </div>
		out.println("</div>");                                          // line 68: </div>
		out.println("</body>");                                         // line 69: </body>
		out.println("</html>");                                         // line 70: </html>
	}


	protected IndexForm form;
	protected HtmlMixin html;
	protected LangMixin lang;
	protected FormTableMixin formTable;
}
