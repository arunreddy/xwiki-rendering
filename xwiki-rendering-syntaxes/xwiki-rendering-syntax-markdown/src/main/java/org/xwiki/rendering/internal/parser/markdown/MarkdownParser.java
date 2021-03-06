/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.rendering.internal.parser.markdown;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;
import org.xwiki.component.annotation.Component;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.rendering.listener.MetaData;
import org.xwiki.rendering.parser.ParseException;
import org.xwiki.rendering.parser.Parser;
import org.xwiki.rendering.syntax.Syntax;

/**
 * Markdown parser based on the <a href="https://github.com/sirthias/pegdown">Pegdown Parser</a>.
 * 
 * @version $Id$
 * @since 4.1M1
 */
@Component
@Named("markdown/1.0")
@Singleton
public class MarkdownParser implements Parser
{
    /**
     * The {@link org.pegdown.PegDownProcessor} used to convert Pegdown documents to HTML.
     */
    private static final PegDownProcessor PEGDOWN_PROCESSOR =
        new PegDownProcessor(Extensions.ALL & ~Extensions.HARDWRAPS);

    /**
     * Pegdown classes can seralize a Markdown tree into XHTML; thus we use our XHMTL parser to convert the XHTML
     * into an XDOM.
     */
    @Inject
    @Named("xhtml/1.0")
    private Parser xhtmlParser;

    @Override
    public Syntax getSyntax()
    {
        return Syntax.MARKDOWN_1_0;
    }

    @Override
    public XDOM parse(Reader source) throws ParseException
    {
        try {
            RootNode rootNode = PEGDOWN_PROCESSOR.parseMarkdown(IOUtils.toString(source).toCharArray());
            String markdownAsHtml = new ToHtmlSerializer().toHtml(rootNode);
            XDOM xdom = this.xhtmlParser.parse(new StringReader("<html><body>" + markdownAsHtml + "</body></html>"));

            // Replace the Syntax MetaData which is set with XHTML with Markdown.
            return new XDOM(xdom.getChildren(),
                new MetaData(Collections.<String, Object>singletonMap(MetaData.SYNTAX, Syntax.MARKDOWN_1_0)));

        } catch (IOException e) {
            throw new ParseException("Failed to convert Markdown to HTML", e);
        }
    }
}
