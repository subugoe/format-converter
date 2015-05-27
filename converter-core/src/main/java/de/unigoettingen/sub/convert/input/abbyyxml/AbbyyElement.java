package de.unigoettingen.sub.convert.input.abbyyxml;

/*

Copyright 2014 SUB Goettingen. All rights reserved.
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader.CurrentPageState;

/**
 * 
 * Represents an element in Abbyy XML. Knows how to deal with the internal model 
 * according to the concrete Abbyy element Implementation.
 *
 */

interface AbbyyElement {

	/**
	 * Can update the current state of the internal model objects.
	 * 
	 * @param current The current state of a model Page, containing the Page itself and its contents
	 * @throws XMLStreamException
	 */
	void updatePageState(CurrentPageState current) throws XMLStreamException;

}
