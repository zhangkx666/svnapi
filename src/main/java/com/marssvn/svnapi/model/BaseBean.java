package com.marssvn.svnapi.model;


import org.springframework.beans.BeanUtils;

import java.io.Serializable;

public class BaseBean implements Serializable {

    /**
     * convert to object of dest class
     * @param destClass dest object class
     * @param <T> object class
     * @return Object
     */
    public <T> T convertTo(Class<T> destClass) {
        try {
            T destObject = destClass.newInstance();
            BeanUtils.copyProperties(this, destObject);
            return destObject;
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * copy properties from source object
     * @param sourceObject source object
     */
    public void copyPropertiesFrom(Object sourceObject) {
        BeanUtils.copyProperties(sourceObject, this);
    }
}
