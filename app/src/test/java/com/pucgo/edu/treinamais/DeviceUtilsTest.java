package com.pucgo.edu.treinamais;

import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.util.DeviceUtils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceUtilsTest {

    @Test
    public void testIsEmulatorNaoLancaNpeComBuildNulo() {
        assertFalse(DeviceUtils.isEmulator());
        assertFalse(ApiClient.isEmulator());
        assertFalse(DeviceUtils.isEmulator(null, null, null, null, null, null, null));
    }

    @Test
    public void testIsEmulatorDetectaPropriedadesDeEmulador() {
        assertTrue(DeviceUtils.isEmulator("generic", "generic", null, null, null, null, null));
        assertTrue(DeviceUtils.isEmulator("google", "emu64xa", "google/sdk_gphone64_x86_64", "ranchu", "sdk_gphone64_x86_64", "Google", "sdk_gphone64_x86_64"));
        assertFalse(DeviceUtils.isEmulator("samsung", "dm1q", "samsung/dm1q/...", "qcom", "SM-S911B", "samsung", "dm1qxxx"));
    }
}
