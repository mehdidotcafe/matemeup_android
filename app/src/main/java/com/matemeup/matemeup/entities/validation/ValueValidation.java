package com.matemeup.matemeup.entities.validation;

import java.util.HashMap;

public interface ValueValidation {
    Boolean validate(Object value, HashMap<String, Object> map);
}
