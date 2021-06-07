package com.cyh.attribute;

import io.netty.util.AttributeKey;
import com.cyh.session.Session;

public interface Attributes {
    AttributeKey<Session> SESSION = AttributeKey.newInstance("session");
}
