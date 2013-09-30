/*
 * Copyright 2003-2004 Michael Franken, Zilverline.
 *
 * The contents of this file, or the files included with this file, are subject to
 * the current version of ZILVERLINE Collaborative Source License for the
 * Zilverline Search Engine (the "License"); You may not use this file except in
 * compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.zilverline.org.
 *
 * See the License for the rights, obligations and
 * limitations governing use of the contents of the file.
 *
 * The Original and Upgraded Code is the Zilverline Search Engine. The developer of
 * the Original and Upgraded Code is Michael Franken. Michael Franken owns the
 * copyrights in the portions it created. All Rights Reserved.
 *
 */

/*
 * Copyright 2003-2004 Michael Franken, Zilverline.
 *
 * Licensed under the Zilverline Collabarative License, Version 1.3.1z (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.zilverline.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zilverline.util;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DOCUMENT ME!
 * 
 * @author michael To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class TestUtils extends TestCase {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestUtils.class);

    /**
     * Constructor for UtilsTest.
     * 
     * @param arg0
     */
    public TestUtils(String arg0) {
        super(arg0);
    }

    public final void testPickOne() {
        for (int i = 0; i < 1000; i++) {
            int j = Utils.pickOne(5);
            assertTrue(j < 5);
            assertTrue(j >= 0);
        }
    }

    public void testCheckISBN() {
        assertFalse(Utils.isValidISBNNumber(null));
        assertFalse(Utils.isValidISBNNumber(""));
        assertFalse(Utils.isValidISBNNumber("just a bit of text"));
        assertFalse(Utils.isValidISBNNumber("0123456789X"));
        assertTrue(Utils.isValidISBNNumber("0764543857"));
        assertTrue(Utils.isValidISBNNumber("0-20130-998-X"));
        assertTrue(Utils.isValidISBNNumber("1-930110-30-8"));
        assertFalse("Need to be ELF proof", Utils.isValidISBNNumber("0-20630-998-X"));
    }

    //
    // /* use this with maven axis code generation on etc/AmazonWebServices.wsdl */
    // public void test4AmazonSearchPortAsinSearchRequest() throws Exception {
    // com.amazon.soap.AmazonSearchBindingStub binding;
    // try {
    // binding = (com.amazon.soap.AmazonSearchBindingStub) new com.amazon.soap.AmazonSearchServiceLocator()
    // .getAmazonSearchPort();
    // } catch (javax.xml.rpc.ServiceException jre) {
    // if (jre.getLinkedCause() != null)
    // jre.getLinkedCause().printStackTrace();
    // throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
    // }
    // assertNotNull("binding is null", binding);
    //
    // // Time out after a minute
    // binding.setTimeout(60000);
    //
    // // Test operation
    // com.amazon.soap.ProductInfo value = null;
    // AsinRequest ar = new com.amazon.soap.AsinRequest();
    // ar.setAsin("159059021X");
    // ar.setType("lite");
    // ar.setTag("zilverline-20");
    // value = binding.asinSearchRequest(ar);
    // // TBD - validate results
    // }

}
