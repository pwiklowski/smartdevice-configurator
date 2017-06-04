package com.wiklosoft.smartdeviceconfigurator;

/**
 * Created by pwiklowski on 03.06.17.
 */

public interface IActionResult {
    void onSuccess(String name);
    void onFailure(String name);
}
