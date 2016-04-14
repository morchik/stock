package com.ktkt.xml;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class ya {
	
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static String readFile(String path) throws IOException {
		return readFile(path, java.nio.charset.StandardCharsets.UTF_8);
	}
	
	public static void writeFile(String path, String msg) throws IOException {
		Files.write(Paths.get(path), msg.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
	
	public static String get_imp_html(String data) throws Exception {
		String result = "<table border=1 >";
		InputSource is = new InputSource(new StringReader(data));

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		//System.out.println(factory);
		DocumentBuilder builder = factory.newDocumentBuilder();
		//System.out.println(builder);
		Document document = builder.parse(is);
		document.normalize();
		//System.out.println(document);
		if (document != null) {
			Element root = document.getDocumentElement();
			//System.out.println(root);
			if (root != null) {
				// Ð´Ð»Ñ� Ð¿Ñ€Ð¾Ñ�Ñ‚Ð¾Ñ‚Ñ‹ Ñ�Ñ€Ð°Ð·Ñƒ Ð±ÐµÑ€ÐµÐ¼ message
				Element message = (Element) root.getElementsByTagName("list")
						.item(0);
				if (message != null) {
					//String textContent = message.getTextContent(); 
					// System.out.println(textContent);
					//System.out.println(message.getAttribute("size"));
					//System.out.println(message.getAttribute("data-count"));
					//System.out.println(message.hasChildNodes());
					// System.out.println("list Attributes count "+message.getAttributes().getLength());
					for (int i = 0; i < message.getElementsByTagName(
							"assigned-task").getLength(); i++) {
						Element tsk = (Element) message.getElementsByTagName(
								"assigned-task").item(i);
						if (tsk != null) {
							// System.out.println(i+"    assigned-task id = "
							// +tsk.getAttribute("id"));
							// System.out.println(i+" close-date "
							// +tsk.getAttribute("close-date"));
							// System.out.println(i+" judgements-count "
							// +tsk.getAttribute("judgements-count"));
							// System.out.println(i+" expiration-date "
							// +tsk.getAttribute("expiration-date"));
							// System.out.println(i+" remaining-hours "
							// +tsk.getAttribute("remaining-hours"));
							// System.out.println(i+" remaining-minutes "
							// +tsk.getAttribute("remaining-minutes"));

							// System.out.println(i+" assigned-task Attributes count "+tsk.getAttributes().getLength());
							Element q = (Element) tsk.getElementsByTagName(
									"query").item(0);
							if (q != null) {
								// System.out.println(i+" query id = "
								// +q.getAttribute("id"));
								// System.out.println(i+" query Attributes count "+q.getAttributes().getLength());
								// System.out.println(i+" query text "+q.getAttribute("text"));

							}
							Element t = (Element) tsk.getElementsByTagName(
									"task").item(0);
							if (t != null) {
								// System.out.println(i+" query id = "
								// +q.getAttribute("id"));
								// System.out.println(i+" query Attributes count "+q.getAttributes().getLength());
								// System.out.println(i+" task judgementitem-count "+t.getAttribute("judgementitem-count"));
							}
							Element r = (Element) q.getElementsByTagName(
									"region").item(0);
							if (r != null) {
								// System.out.println(i+" query id = "
								// +q.getAttribute("id"));
								// System.out.println(i+" query Attributes count "+q.getAttributes().getLength());
								// System.out.println(i+" region name "+r.getAttribute("name"));
							}
							Element tf = (Element) tsk.getElementsByTagName("tasksetfactory").item(0);
							
							String str = "<tr><th>" + q.getAttribute("text")
									+ " </th> <th> ( "
									+ tsk.getAttribute("judgements-count")
									+ " / "
									+ t.getAttribute("judgementitem-count")
									+ " ) </th> <th>"
									+ tsk.getAttribute("remaining-hours") + " : "
									+ tsk.getAttribute("remaining-minutes")
									+ "  </th> <th> " + r.getAttribute("cname")+ " / "
											+ r.getAttribute("code")
									+ "  </th> <th> " + tsk.getAttribute("task-status")+ " / "
										+ t.getAttribute("assessment-status")
									+ "  </th> <th> " + tsk.getAttribute("expiration-date")
									+ "  </th> <th> " + q.getAttribute("device")
									+ "  </th> <th> " + tf.getAttribute("name")	+ " / "								
										+  t.getAttribute("task-type")
									+ "  </th> <th> " + tsk.getAttribute("ticks")
									+ "</th></tr>";
							result += str+"\r\n";
							//System.out.println(str);
						}
					}
				}
			}
		}
		return result+"</table>";
	}
}
