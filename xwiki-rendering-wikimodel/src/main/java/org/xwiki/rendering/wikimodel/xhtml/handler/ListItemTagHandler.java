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
package org.xwiki.rendering.wikimodel.xhtml.handler;

import org.xwiki.rendering.wikimodel.xhtml.impl.XhtmlHandler;

/**
 * @version $Id$
 * @since 4.0M1
 */
public class ListItemTagHandler extends TagHandler
{
    public ListItemTagHandler()
    {
        super(true, false, true);
    }

    public ListItemTagHandler(
        boolean documentContainer,
        boolean requiresDocument,
        boolean contentContainer)
    {
        super(documentContainer, requiresDocument, contentContainer);
    }

    @Override
    public void begin(XhtmlHandler.TagStack.TagContext context)
    {
        String markup = context.getParent().getName().equals("ol") ? "#" : "*";
        begin(markup, context);
    }

    protected void begin(String markup, XhtmlHandler.TagStack.TagContext context)
    {
        StringBuffer listStyles = (StringBuffer) context
            .getTagStack()
            .getStackParameter("listStyles");
        listStyles.append(markup);
        context.getScannerContext().beginListItem(listStyles.toString());
    }

    @Override
    public void end(XhtmlHandler.TagStack.TagContext context)
    {
        StringBuffer listStyles = (StringBuffer) context
            .getTagStack()
            .getStackParameter("listStyles");
        // We should always have a length greater than 0 but we handle
        // the case where the user has entered some badly formed HTML
        if (listStyles.length() > 0) {
            listStyles.setLength(listStyles.length() - 1);
        }
        // Note: Do not generate an endListItem() event since it'll be generated
        // automatically by the next element.
    }
}
