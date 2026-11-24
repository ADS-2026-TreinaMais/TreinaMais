package com.pucgo.edu.treinamais.util;

import android.os.Build;

/**
 * Utilitários para inspeção segura de propriedades do dispositivo Android em tempo de execução.
 */
public final class DeviceUtils {

    private DeviceUtils() {
    }

    /**
     * Indica se o aplicativo está rodando em um emulador Android.
     * Seguro contra valores nulos em testes unitários na JVM.
     */
    public static boolean isEmulator() {
        return isEmulator(
                Build.BRAND,
                Build.DEVICE,
                Build.FINGERPRINT,
                Build.HARDWARE,
                Build.MODEL,
                Build.MANUFACTURER,
                Build.PRODUCT
        );
    }

    public static boolean isEmulator(
            String brand,
            String device,
            String fingerprint,
            String hardware,
            String model,
            String manufacturer,
            String product
    ) {
        return (startsWith(brand, "generic") && startsWith(device, "generic"))
                || startsWith(fingerprint, "generic")
                || startsWith(fingerprint, "unknown")
                || contains(hardware, "goldfish")
                || contains(hardware, "ranchu")
                || contains(model, "google_sdk")
                || contains(model, "Emulator")
                || contains(model, "Android SDK built for x86")
                || contains(manufacturer, "Genymotion")
                || contains(product, "sdk_google")
                || contains(product, "google_sdk")
                || contains(product, "sdk")
                || contains(product, "sdk_x86")
                || contains(product, "vbox86p")
                || contains(product, "emulator")
                || contains(product, "simulator");
    }

    private static boolean startsWith(String value, String prefix) {
        return value != null && value.startsWith(prefix);
    }

    private static boolean contains(String value, String substring) {
        return value != null && value.contains(substring);
    }
}
