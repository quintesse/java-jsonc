package org.codejive.jsonc;

/**
 * Beans that support customized output of JSON text shall implement this interface.
 *
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface JsonAware {
    /** @return JSON text */
    String toJSONString();
}
