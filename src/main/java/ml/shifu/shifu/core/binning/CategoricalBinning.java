/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ml.shifu.shifu.core.binning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CategoricalBinning class
 * 
 * @Oct 20, 2014
 *
 */
public class CategoricalBinning extends AbstractBinning<String> {
    
    private final static Logger log = LoggerFactory.getLogger(CategoricalBinning.class);
    
    private Set<String> categoricalVals;
    
    /**
     * Empty constructor : it is just for bin merging
     */
    protected CategoricalBinning(){}
    
    /**
     * Constructor with expected bin number. 
     * For categorical variable, the binningNum won't be used
     * @param binningNum
     */
    public CategoricalBinning(int binningNum) {
        this(binningNum, null);
    }

    /**
     * Constructor with expected bin number and missing value list
     * For categorical variable, the binningNum won't be used
     * @param binningNum
     * @param missingValList
     */
    public CategoricalBinning(int binningNum, List<String> missingValList) {
        super(binningNum, missingValList);
        this.categoricalVals = new HashSet<String>();
    }

    /* (non-Javadoc)
      * Add the string into value set
     * First of all the input string will be trimmed and check whether it is missing value or not
     * If it is missing value, the missing value count will +1
     * @see ml.shifu.shifu.core.binning.AbstractBinning#addData(java.lang.Object)
     */
    @Override
    public void addData(String val) {
        String fval = StringUtils.trimToEmpty(val);
        if ( !isMissingVal(fval) ) {
            categoricalVals.add(fval);
        } else {
            super.incMissingValCnt();
        }
    }

    /* (non-Javadoc)
     * @see ml.shifu.shifu.core.binning.AbstractBinning#getDataBin()
     */
    @Override
    public List<String> getDataBin() {
        List<String> binningVals = new ArrayList<String>();
        binningVals.addAll(categoricalVals);
        return binningVals;
    }

    /* (non-Javadoc)
     * @see ml.shifu.shifu.core.binning.AbstractBinning#mergeBin(ml.shifu.shifu.core.binning.AbstractBinning)
     */
    @Override
    public void mergeBin(AbstractBinning<?> another) {
       CategoricalBinning binning = (CategoricalBinning) another;
       super.mergeBin(another);
       
       this.categoricalVals.addAll(binning.categoricalVals);
    }
    
    /**
     * convert @CategoricalBinning to String
     * @return
     */
    protected void stringToObj(String objValStr) {
        super.stringToObj(objValStr);

        if ( categoricalVals == null ) {
            categoricalVals = new HashSet<String>();
        } else {
            categoricalVals.clear();
        }
        
        String[] objStrArr = objValStr.split(Character.toString(FIELD_SEPARATOR), -1);        
        if ( objStrArr.length > 4 && StringUtils.isNotBlank(objStrArr[4]) ) {
            String[] elements = objStrArr[4].split(Character.toString(SETLIST_SEPARATOR), -1);
            for ( String element : elements ) {
                categoricalVals.add(element);
            }
        } else {
            log.warn("Empty categorical bin - " + objValStr);
        }
    }
    
    /**
     * convert @CategoricalBinning to String
     * @return
     */
    public String objToString() {
        return super.objToString() 
                + Character.toString(FIELD_SEPARATOR) 
                + StringUtils.join(categoricalVals, SETLIST_SEPARATOR);
    }

}
