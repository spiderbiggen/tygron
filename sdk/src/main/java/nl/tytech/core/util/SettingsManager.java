/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.serializable.TokenPair;
import nl.tytech.data.core.item.Item;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.TLanguage;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.util.Base64;
import nl.tytech.util.StringUtils;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.logger.TLogger;

/**
 * @author Jeroen Warmerdam
 */
public class SettingsManager {

    public enum AssetType {

        /**
         * Assets that are packed into the client app. These assets should always be used (e.g. panel xmls and fonts that are always used).
         */
        PACKED,

        /**
         * Assets that are streamed over the Internet. These assets are used in specific game (e.g. models, images).
         */
        STREAM,

        /**
         * Assets that are only used by the Server app. Thinks like game xmls or geo data.
         */
        SERVER,

        /**
         * Assets that are uploaded by the users.
         */
        UPLOAD;

        private final static String DEFAULT_ECPLISE_GENERIC_ASSETS_FOLDER = ".." + File.separator + "engine_assets" + File.separator;

        private final static String DEFAULT_ECPLISE_ASSETS_FOLDER = ".." + File.separator + "engine_distribution" + File.separator;

        public String getDevFolder() {
            return DEFAULT_ECPLISE_ASSETS_FOLDER + StringUtils.capitalize(this.toString()) + File.separator;
        }

        public String getDevGenericDefault() {
            return DEFAULT_ECPLISE_GENERIC_ASSETS_FOLDER + StringUtils.capitalize(this.toString()) + File.separator;
        }
    }

    /**
     * End-User Conditions
     * @author Maxim
     *
     */
    public enum Conditions {

        NL("end_user_conditions_nl.txt"),

        EN("end_user_conditions_en.txt"), ;

        private String location;

        private Conditions(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }
    }

    /**
     * Three levels of run mode
     *
     * @author Maxim Knepfle
     */
    public enum RunMode {
        /**
         * Release version, goes to the customer
         */
        RELEASE,
        /**
         * Normal mode used for development in Eclipse/IDE
         */
        DEV,
        /**
         * Special debug mode used to debug JME/the scene
         */
        DEBUG,
        /**
         * Shows the model batches in the world with random colours.
         */
        BATCHING,
        /**
         * Dev mode that double default fps
         */
        FPS2;
    }

    /**
     * Save a timestamp to the windows reg every X secs.
     * @author Maxim
     *
     */
    private class SettingsActive extends Thread {

        private static final int PERIOD = 500;

        /**
         * Monkey style name of timestamp key
         */
        private final String timeStampKey;

        private final Preferences prefs;

        public volatile boolean active = true;

        private SettingsActive(String storageID) {

            this.setName("Daemon-SettingsSync");
            this.setDaemon(true);

            /**
             * Read values
             */
            timeStampKey = "S_" + convertToAppSettingsKey(SettingsType.TIMESTAMP, -1);
            prefs = Preferences.userRoot().node(storageID);
        }

        @Override
        public void run() {
            while (active) {
                saveTimeStamp();
                ThreadUtils.sleepInterruptible(PERIOD);
            }
            TLogger.info("Stopped saving timestamps!");
        }

        private void saveTimeStamp() {

            /**
             * Skip normal saving procedure, this is only 1 value and is faster.
             */
            try {
                prefs.put(timeStampKey, Long.toString(System.currentTimeMillis()));
                prefs.sync();
            } catch (Exception e) {
                TLogger.exception(e);
            }
        }
    }

    /**
     * Event signaling that a _setting is updated. This event contains the _setting type and the new value.
     */
    public static enum SettingsEventType implements EventTypeEnum {

        SETTINGS_UPDATED(SettingsType.class, Object.class),

        PROJECT_NAME_UPDATED(String.class);

        private List<Class<?>> classes;

        private SettingsEventType(Class<?>... classes) {
            this.classes = Arrays.asList(classes);
        }

        @Override
        public boolean canBePredefined() {
            return false;
        }

        @Override
        public List<Class<?>> getClasses() {
            return classes;
        }

        @Override
        public Class<?> getResponseClass() {
            return null;
        }

        @Override
        public boolean isServerSide() {
            return false;
        }

    }

    /**
     * An enum defining the type of setting. This is used as a key in the properties file.
     */
    public static enum SettingsType {

        // TODO:Maxim: link default to settingspanel defaults and remove null values and make this default in loading app

        CONDITIONS_CHECKSUM(""),

        CONDITIONS_REGION(""),

        TIMESTAMP("" + System.currentTimeMillis()),

        ASSET_CONFIG_URL("Config/AssetConfig.cfg"),

        WATERMARK("Copyright TyTech BV"),

        CLIENT_TOKEN(StringUtils.randomToken()),

        ONLINE_USERNAME(""),

        ONLINE_PASSWORD(""),

        RUNMODE(RunMode.RELEASE),

        FULLSCREEN(true),

        TITLE("TyTech Engine"),

        LOGTOFILE(false),

        SERVER_SLOT_ID(-1),

        FORCE_NATIVE_RES(true),

        SERVER_AMOUNT_OF_CONNECTIONS(-1),

        SERVER_TOKEN(StringUtils.randomToken()),

        SCREEN_SHADOWS(1),

        SCREEN_ANTI_ALIASING(0),

        SCREEN_BLOOM(0),

        SCREEN_WATER(1),

        SCREEN_DOF(0),

        SCREEN_MIN_AMOUNT_MODELS(800),

        SCREEN_ACTUAL_AMOUNT_MODELS(800),

        SCREEN_SSAO(0),

        TOUCHTABLE_INTERFACE(false),

        SESSION_TYPE(Network.SessionType.SINGLE),

        TLANGUAGE(TLanguage.EN),

        SOUND_PERCENTAGE(1f),

        SCREEN_TEXTURE_SIZE(TextureSize.MEDIUM),

        SCREEN_CARTOON(0),

        SCREEN_HDR(0),

        SCREEN_SCATTERING(1),

        V_SYNC(true),

        SHOW_INTRO_TUTORIAL(true),

        FRAME_RATE(30),

        ONLINE_ACCOUNT_DOMAIN(""),

        /**
         * OVerride AppSettings renderer
         */
        RENDERER("LWJGL-OpenGL2"),

        SERVER_KEY(""),

        VIDEO_DIRECTORY(System.getProperty("user.home")),

        LAST_EDITOR_TLANGUAGE(TLanguage.NL),

        LAST_EDITOR_UNIT_SYSTEM_TYPE(UnitSystemType.SI),

        LAST_EDITOR_TCURRENCY(TCurrency.EURO),

        PRELOADER_VERSION_SKIPPED(-1),

        ;

        private Object defaultValue;

        private SettingsType(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return this.defaultValue;
        }
    }

    protected static class SingletonHolder {
        public static SettingsManager INSTANCE;

        public static SettingsManager getInstance() {
            if (INSTANCE == null) {
                TLogger.showstopper("SettingsManager has not been setup yet. Use SettingsManager.setup() before calling any other method.");
            }
            return INSTANCE;
        }
    }

    /**
     * Size of a texture (Width x Height) in three versions.
     */
    public enum TextureSize {
        /**
         * Texture at 25% of the original Width and Height.
         */
        SMALL(128),
        /**
         * Texture at 50% of the original Width and Height.
         */
        MEDIUM(256),
        /**
         * Texture with same size (Width and Height) as the original.
         */
        LARGE(512);

        private int instancePixels;

        private TextureSize(int instancePixels) {
            this.instancePixels = instancePixels;
        }

        public int getMaxInstancePixels() {
            return instancePixels;
        }
    }

    private static final String DEFAULT_JME_FILE_LOC = "Config/SettingsDefaults.xml";

    public static final String ROOT_LOCATION = "TyTech/Engine";

    public final static double GUI_DESIGN_WIDTH = 1280d;

    public final static double GUI_DESIGN_HEIGHT = 650d;

    public static int getAmountOfConnections() {
        return SingletonHolder.getInstance()._getAmountOfConnections();
    }

    public static Network.AppType getApplicationType() {
        return SingletonHolder.getInstance()._getApplicationType();
    }

    public static String getClientToken(int connectionID) {
        return SingletonHolder.getInstance()._getClientToken(connectionID);
    }

    public static String getConditionsChecksum() {
        return SingletonHolder.getInstance()._getConditionsChecksum();
    }

    public static Conditions getConditionsRegion() {
        return SingletonHolder.getInstance()._getConditionsRegion();
    }

    public static int getFrameRate() {
        return SingletonHolder.getInstance()._getFrameRate();
    }

    public static String getHardwareScore(SettingsType type) {
        return SingletonHolder.getInstance()._getHardwareScore(type);
    }

    public static int getInstanceID() {
        return SingletonHolder.getInstance()._getInstanceID();
    }

    public static TLanguage getLanguage() {
        return SingletonHolder.getInstance()._getLanguage();
    }

    public static TCurrency getLastEditorTCurrency() {
        return SingletonHolder.getInstance()._getLastEditorTCurrency();
    }

    public static TLanguage getLastEditorTLanguage() {
        return SingletonHolder.getInstance()._getLastEditorTLanguage();
    }

    public static UnitSystemType getLastEditorUnitSystemType() {
        return SingletonHolder.getInstance()._getLastEditorUnitSystemType();
    }

    public static String getOnlineAccountDomain() {
        return SingletonHolder.getInstance()._getOnlineAccountDomain();
    }

    public static String getOnlinePassword() {
        return SingletonHolder.getInstance()._getOnlinePassword();
    }

    public static String getOnlineUserName() {
        return SingletonHolder.getInstance()._getOnlineUsername();
    }

    public static int getPreloaderSkippedVersion() {
        return SingletonHolder.getInstance()._getPreloaderSkippedVersion();
    }

    public static String getProjectName() {
        return SingletonHolder.getInstance()._getProjectName();
    }

    protected static <T> T getPropertyDirect(String key, Class<T> classz) {
        return SingletonHolder.getInstance().getProperty(key, classz);
    }

    public static String getRenderer() {
        return SingletonHolder.getInstance()._getRenderer();
    }

    public static RunMode getRunMode() {
        return SingletonHolder.getInstance()._getRunMode();
    }

    public static int getScreenActualAmountModels() {
        return SingletonHolder.getInstance()._getScreenActualAmountModels();
    }

    public static int getScreenAntiAliasing() {
        return SingletonHolder.getInstance()._getScreenAntiAliasing();
    }

    public static int getScreenBloom() {
        return SingletonHolder.getInstance()._getScreenBloom();
    }

    public static int getScreenCartoon() {
        return SingletonHolder.getInstance()._getScreenCartoon();
    }

    public static int getScreenDoF() {
        return SingletonHolder.getInstance()._getScreenDoF();
    }

    public static int getScreenHDR() {
        return SingletonHolder.getInstance()._getScreenHDR();
    }

    public static int getScreenHeight() {
        return SingletonHolder.getInstance()._getScreenHeight();
    }

    public static int getScreenMinAmountModels() {
        return SingletonHolder.getInstance()._getScreenMinAmountModels();
    }

    public static double getScreenScale() {
        return getScreenWidth() / GUI_DESIGN_WIDTH;
    }

    public static double getScreenScaledHeight() {
        return getScreenHeight() / getScreenScale();
    }

    public static double getScreenScaledWidth() {
        return GUI_DESIGN_WIDTH;
    }

    public static int getScreenScattering() {
        return SingletonHolder.getInstance()._getScreenScattering();
    }

    public static int getScreenShadows() {
        return SingletonHolder.getInstance()._getScreenShadows();
    }

    public static int getScreenSSAO() {
        return SingletonHolder.getInstance()._getScreenSSAO();
    }

    public static TextureSize getScreenTextureSize() {
        return SingletonHolder.getInstance()._getScreenTextureSize();
    }

    public static int getScreenWater() {
        return SingletonHolder.getInstance()._getScreenWater();
    }

    public static int getScreenWidth() {
        return SingletonHolder.getInstance()._getScreenWidth();
    }

    /**
     * _get the IP address of the server in String form
     *
     * @return the IP Address
     */
    public static String getServerIP() {
        return SingletonHolder.getInstance()._getServerIP();
    }

    public final static String getServerKey() {
        return SingletonHolder.INSTANCE._getServerKey();
    }

    /**
     * _get the game ID of the server in String form
     *
     * @return the game ID
     */
    public static int getServerSlotID(int connectionID) {
        return SingletonHolder.getInstance()._getServerSlotID(connectionID);
    }

    /**
     * _get the server token
     *
     * @return token
     */
    public static String getServerToken(int connectionID) {
        return SingletonHolder.getInstance()._getServerToken(connectionID);
    }

    public static float getSoundVolumePercentage() {
        return SingletonHolder.getInstance()._getSoundVolumePercentage();
    }

    public static Map<String, Object> getStorage() {
        return SingletonHolder.getInstance()._getStorage();
    }

    public static String getTitle() {
        return SingletonHolder.getInstance()._getTitle();

    }

    public static List<TokenPair> getTokenPairs() {
        return SingletonHolder.getInstance()._getTokenPairs();
    }

    public static String getVideoDirectory() {
        return SingletonHolder.getInstance()._getVideoDirectory();
    }

    public static boolean isDebug() {
        return SingletonHolder.getInstance()._isDebug();
    }

    public static boolean isForceNativeResolution() {
        return SingletonHolder.getInstance()._isForceNativeResolution();
    }

    public static boolean isLogToFile() {
        return SingletonHolder.getInstance()._isLogToFile();
    }

    /**
     * When true app supports a oculs switch
     * @return
     */
    public static boolean isOculusMode() {
        return SingletonHolder.getInstance()._isOculusMode();
    }

    public static boolean isShowTutorial() {
        return SingletonHolder.getInstance()._isShowTutorial();
    }

    public static boolean isStayLoggedIn() {
        return SingletonHolder.getInstance()._isStayLoggedIn();
    }

    public static boolean isTouchtableInterface() {
        return SingletonHolder.getInstance()._isTouchtableInterface();
    }

    public static boolean isVSync() {
        return SingletonHolder.getInstance()._isVSync();
    }

    public static void setAmountOfConnections(int noGames) {
        SingletonHolder.getInstance()._setAmountOfConnections(noGames);
    }

    public static void setApplicationType(Network.AppType type) {
        SingletonHolder.getInstance()._setApplicationType(type);
    }

    public static void setClientToken(int connectionID, String clientToken) {
        SingletonHolder.getInstance()._setClientToken(connectionID, clientToken);
    }

    public static void setConditionsChecksum(Conditions conditions, String eulaChecksum) {
        SingletonHolder.getInstance()._setConditionsChecksum(conditions, eulaChecksum);
    }

    public static void setHardwareScore(SettingsType type, String data) {
        SingletonHolder.getInstance()._setHardwareScore(type, data);
    }

    public static void setLanguage(TLanguage language) {
        SingletonHolder.getInstance()._setLanguage(language);
    }

    public static void setLastEditorTCurrency(TCurrency currency) {
        SingletonHolder.getInstance()._setLastEditorTCurrency(currency);
    }

    public static void setLastEditorTLanguage(TLanguage language) {
        SingletonHolder.getInstance()._setLastEditorTLanguage(language);
    }

    public static void setLastEditorUnitSystemType(UnitSystemType unitSystemType) {
        SingletonHolder.getInstance()._setLastEditorUnitSystemType(unitSystemType);
    }

    public static void setLogToFile(boolean logToFile) {
        SingletonHolder.getInstance()._setLogToFile(logToFile);
    }

    public static void setOculusMode(boolean oculus) {
        SingletonHolder.getInstance()._setOculusMode(oculus);
    }

    public static void setOnlineAccountDomain(String domain) {
        SingletonHolder.getInstance()._setOnlineAccountDomain(domain);
    }

    public static void setOnlinePassword(String passwd) {
        SingletonHolder.getInstance()._setOnlinePassword(passwd);
    }

    public static void setOnlineUserName(String username) {
        SingletonHolder.getInstance()._setOnlineUserName(username);
    }

    public static void setPreloaderSkippedVersion(int version) {
        SingletonHolder.getInstance()._setPreloaderSkippedVersion(version);
    }

    public static void setProjectName(String gameName) {
        SingletonHolder.getInstance()._setProjectName(gameName);
    }

    protected static void setPropertyDirect(String key, Object value) {
        SingletonHolder.getInstance().setProperty(key, value);
    }

    public static void setRenderer(String renderer) {
        SingletonHolder.getInstance()._setRenderer(renderer);
    }

    public static void setRunMode(RunMode level) {
        SingletonHolder.getInstance()._setRunMode(level);
    }

    public static void setScreenActualAmountModels(int drawDistance) {
        SingletonHolder.getInstance()._setScreenActualAmountModels(drawDistance);
    }

    public static void setScreenAntiAliasing(int antiAliasing) {
        SingletonHolder.getInstance()._setScreenAntiAliasing(antiAliasing);
    }

    public static void setScreenBloom(int bloom) {
        SingletonHolder.getInstance()._setScreenBloom(bloom);
    }

    public static void setScreenCartoon(int hdr) {
        SingletonHolder.getInstance()._setScreenCartoon(hdr);
    }

    public static void setScreenDoF(int dof) {
        SingletonHolder.getInstance()._setScreenDoF(dof);
    }

    public static void setScreenHDR(int hdr) {
        SingletonHolder.getInstance()._setScreenHDR(hdr);
    }

    public static void setScreenHeight(int height) {
        SingletonHolder.getInstance()._setScreenHeight(height);
    }

    public static void setScreenMinAmountModels(int drawDistance) {
        SingletonHolder.getInstance()._setScreenMinAmountModels(drawDistance);
    }

    public static void setScreenScattering(int value) {
        SingletonHolder.getInstance()._setScreenScattering(value);
    }

    public static void setScreenShadows(int shadows) {
        SingletonHolder.getInstance()._setScreenShadows(shadows);
    }

    public static void setScreenSSAO(int bloom) {
        SingletonHolder.getInstance()._setScreenSSAO(bloom);
    }

    public static void setScreenTextureSize(TextureSize textureQuality) {
        SingletonHolder.getInstance()._setScreenTextureSize(textureQuality);
    }

    public static void setScreenWater(int waterQuality) {
        SingletonHolder.getInstance()._setScreenWater(waterQuality);
    }

    public static void setScreenWidth(int width) {
        SingletonHolder.getInstance()._setScreenWidth(width);
    }

    /**
     * _set the IP Address for the server
     *
     * @param serverIP
     */
    public static void setServerIP(String serverIP) {

        SingletonHolder.getInstance()._setServerIP(serverIP);
    }

    public static void setServerKey(String key) {
        SingletonHolder.getInstance()._setServerKey(key);
    }

    /**
     * _set the game ID for the server
     *
     * @param serverGameSlotID
     */
    public static void setServerSlotID(int connectionID, Integer serverGameSlotID) {
        SingletonHolder.getInstance()._setServerSlotID(connectionID, serverGameSlotID);
    }

    public static void setServerToken(int connectionID, String serverToken) {
        SingletonHolder.getInstance()._setServerToken(connectionID, serverToken);
    }

    public static void setShowTutorial(boolean showTutorial) {
        SingletonHolder.getInstance()._setShowTutorial(showTutorial);
    }

    public static void setSoundVolumePercentage(Float percentage) {
        SingletonHolder.getInstance()._setSoundVolumePercentage(percentage);
    }

    public static void setStayLoggedIn(boolean stayLoggedIn) {
        SingletonHolder.getInstance()._setStayLoggedIn(stayLoggedIn);
    }

    public static void setTitle(String title) {
        SingletonHolder.getInstance()._setTitle(title);
    }

    public static void setTouchtableInterface(boolean touch) {
        SingletonHolder.getInstance()._setTouchtableInterface(touch);
    }

    public static synchronized void setup(Class<? extends SettingsManager> classz, Network.AppType applicationType) {

        if (SingletonHolder.INSTANCE != null && SingletonHolder.INSTANCE.getClass() == classz
                && SingletonHolder.INSTANCE._getApplicationType() == applicationType) {
            TLogger.severe("SettingsManager already setup. ");
            return;
        }

        try {
            // get empty constructor
            Constructor<? extends SettingsManager> constructor = classz.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            SettingsManager newInstance = constructor.newInstance();
            constructor.setAccessible(false);
            newInstance.applicationType = applicationType;

            if (SingletonHolder.INSTANCE == null) {
                // first time init.
                newInstance.firstTimeInit();
            } else {
                // second time recyle storage and storageID
                newInstance.storage.putAll(SingletonHolder.INSTANCE.storage);
                newInstance.storageID = SingletonHolder.INSTANCE.storageID;
                newInstance.instanceID = SingletonHolder.INSTANCE.instanceID;
                newInstance.serverIP = SingletonHolder.INSTANCE.serverIP;
                newInstance.oculusMode = SingletonHolder.INSTANCE.oculusMode;
            }
            SingletonHolder.INSTANCE = newInstance;

        } catch (Exception e) {
            TLogger.exception(e, "Cannot load SettingsManager.");
        }

        if (getRunMode() == RunMode.RELEASE && !SingletonHolder.INSTANCE.isReleaseAllowed()) {
            RunMode targetMode = RunMode.DEV;
            TLogger.warning("This code is being run from source. The TyTech Engine will not allow " + RunMode.RELEASE
                    + " runmode. Switching to " + targetMode);
            setRunMode(targetMode);
        }

        TLogger.setLogToFile(isLogToFile());
    }

    public static void setVideoDirectory(String path) {
        SingletonHolder.getInstance()._setVideoDirectory(path);

    }

    public static void setVSync(boolean vsync) {
        SingletonHolder.getInstance()._setVSync(vsync);
    }

    public static void stopSlotKeeper() {
        SingletonHolder.getInstance()._stopSlotKeeper();
    }

    protected String storageID;

    protected int instanceID;

    protected final Map<String, Object> storage;

    private Network.AppType applicationType;

    private String projectName = null;

    /**
     * Not stored in registry, these vars are not useful in a windowed environment.
     */
    private volatile int screenWidth = 1280;

    private volatile int screenHeight = 800;

    private volatile String serverIP = "localhost";

    private boolean stayLoggedIn = true;

    private SettingsActive settingsActive;

    private volatile boolean oculusMode = false;

    protected SettingsManager() {
        this(new HashMap<>());
    }

    protected SettingsManager(Map<String, Object> storage) {
        this.storage = storage;
    }

    private int _getAmountOfConnections() {

        /**
         * Viewer and editor are always 1 connection
         */
        Network.AppType appType = _getApplicationType();
        if (appType == AppType.PARTICIPANT || appType == AppType.EDITOR) {
            return 1;
        }
        return getProperty(SettingsType.SERVER_AMOUNT_OF_CONNECTIONS, Integer.class);
    }

    private Network.AppType _getApplicationType() {
        return this.applicationType;
    }

    /**
     * _get the client token
     *
     * @return token
     */
    private String _getClientToken(int connectionID) {
        return getProperty(SettingsType.CLIENT_TOKEN, connectionID, String.class);
    }

    private String _getConditionsChecksum() {
        return getProperty(SettingsType.CONDITIONS_CHECKSUM, String.class);
    }

    private Conditions _getConditionsRegion() {
        String data = getProperty(SettingsType.CONDITIONS_REGION, String.class);
        try {
            return Conditions.valueOf(data);
        } catch (Exception e) {
            return null;
        }
    }

    private int _getFrameRate() {
        return getProperty(SettingsType.FRAME_RATE, Integer.class);
    }

    private String _getHardwareScore(SettingsType type) {
        return getProperty(type, String.class);
    }

    private int _getInstanceID() {
        return instanceID;
    }

    private TLanguage _getLanguage() {
        return getProperty(SettingsType.TLANGUAGE, TLanguage.class);
    }

    private TCurrency _getLastEditorTCurrency() {
        return getProperty(SettingsType.LAST_EDITOR_TCURRENCY, TCurrency.class);
    }

    private TLanguage _getLastEditorTLanguage() {
        return getProperty(SettingsType.LAST_EDITOR_TLANGUAGE, TLanguage.class);
    }

    private UnitSystemType _getLastEditorUnitSystemType() {
        return getProperty(SettingsType.LAST_EDITOR_UNIT_SYSTEM_TYPE, UnitSystemType.class);
    }

    private String _getOnlineAccountDomain() {
        return getProperty(SettingsType.ONLINE_ACCOUNT_DOMAIN, String.class);
    }

    /**
     * Password is stored in base64, this is not safe!
     */
    private String _getOnlinePassword() {
        String value = getProperty(SettingsType.ONLINE_PASSWORD, String.class);
        if (value == null) {
            return StringUtils.EMPTY;
        }

        try {
            return new String(Base64.decode(value));
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    private String _getOnlineUsername() {
        return getProperty(SettingsType.ONLINE_USERNAME, String.class);

    }

    private int _getPreloaderSkippedVersion() {
        return getProperty(SettingsType.PRELOADER_VERSION_SKIPPED, Integer.class);
    }

    private String _getProjectName() {
        return projectName;
    }

    private String _getRenderer() {
        return getProperty(SettingsType.RENDERER, String.class);
    }

    private RunMode _getRunMode() {
        return getProperty(SettingsType.RUNMODE, RunMode.class);
    }

    private int _getScreenActualAmountModels() {
        return getProperty(SettingsType.SCREEN_ACTUAL_AMOUNT_MODELS, Integer.class);
    }

    private int _getScreenAntiAliasing() {
        return getProperty(SettingsType.SCREEN_ANTI_ALIASING, Integer.class);
    }

    private int _getScreenBloom() {
        return getProperty(SettingsType.SCREEN_BLOOM, Integer.class);
    }

    private int _getScreenCartoon() {
        return getProperty(SettingsType.SCREEN_CARTOON, Integer.class);
    }

    private int _getScreenDoF() {
        return getProperty(SettingsType.SCREEN_DOF, Integer.class);
    }

    private int _getScreenHDR() {
        return getProperty(SettingsType.SCREEN_HDR, Integer.class);
    }

    private int _getScreenHeight() {
        return screenHeight;
    }

    private int _getScreenMinAmountModels() {
        return getProperty(SettingsType.SCREEN_MIN_AMOUNT_MODELS, Integer.class);
    }

    private int _getScreenScattering() {
        return getProperty(SettingsType.SCREEN_SCATTERING, Integer.class);
    }

    private int _getScreenShadows() {
        return getProperty(SettingsType.SCREEN_SHADOWS, Integer.class);
    }

    private int _getScreenSSAO() {
        return getProperty(SettingsType.SCREEN_SSAO, Integer.class);
    }

    private TextureSize _getScreenTextureSize() {
        return getProperty(SettingsType.SCREEN_TEXTURE_SIZE, TextureSize.class);
    }

    private int _getScreenWater() {
        return getProperty(SettingsType.SCREEN_WATER, Integer.class);
    }

    private int _getScreenWidth() {
        return screenWidth;
    }

    /**
     * _get the IP address of the server in String form
     *
     * @return the IP Address
     */
    private String _getServerIP() {
        return serverIP;
    }

    private String _getServerKey() {
        return getProperty(SettingsType.SERVER_KEY, String.class);
    }

    /**
     * _get the game ID of the server in String form
     *
     * @return the game ID
     */
    private int _getServerSlotID(int connectionID) {
        return getProperty(SettingsType.SERVER_SLOT_ID, connectionID, Integer.class);
    }

    /**
     * _get the server token
     *
     * @return token
     */
    private String _getServerToken(int connectionID) {
        return getProperty(SettingsType.SERVER_TOKEN, connectionID, String.class);
    }

    private float _getSoundVolumePercentage() {
        return getProperty(SettingsType.SOUND_PERCENTAGE, Float.class);
    }

    protected Map<String, Object> _getStorage() {
        return storage;
    }

    private String _getTitle() {
        return getProperty(SettingsType.TITLE, String.class);
    }

    private List<TokenPair> _getTokenPairs() {

        List<TokenPair> tokenPairs = new ArrayList<TokenPair>();
        int connections = this._getAmountOfConnections() > 0 ? this._getAmountOfConnections() : 1;
        Preferences prefs = Preferences.userRoot().node(storageID);

        try {
            String[] keys = prefs.keys();

            for (int i = 0; i < connections; i++) {

                String clientTokenKey = StringUtils.capitalizeWithUnderScores(SettingsType.CLIENT_TOKEN.toString()) + "-" + i;
                String serverTokenKey = StringUtils.capitalizeWithUnderScores(SettingsType.SERVER_TOKEN.toString()) + "-" + i;

                String clientToken = null;
                String serverToken = null;

                for (String key : keys) {
                    if (key.contains(clientTokenKey)) {
                        clientToken = prefs.get(key, null);
                    }
                    if (key.contains(serverTokenKey)) {
                        serverToken = prefs.get(key, null);
                    }
                }
                if (clientToken != null && serverToken != null) {
                    tokenPairs.add(new TokenPair(serverToken, clientToken));
                }
            }
        } catch (Exception e) {
        }
        return tokenPairs.size() == 0 ? null : tokenPairs;
    }

    private String _getVideoDirectory() {
        return getProperty(SettingsType.VIDEO_DIRECTORY, String.class);
    }

    private boolean _isDebug() {
        return getProperty(SettingsType.RUNMODE, RunMode.class) == RunMode.DEBUG;
    }

    private boolean _isForceNativeResolution() {
        return getProperty(SettingsType.FORCE_NATIVE_RES, Boolean.class);
    }

    private boolean _isLogToFile() {
        return getProperty(SettingsType.LOGTOFILE, Boolean.class);
    }

    private boolean _isOculusMode() {
        return oculusMode;
    }

    private boolean _isShowTutorial() {
        return getProperty(SettingsType.SHOW_INTRO_TUTORIAL, Boolean.class);
    }

    private boolean _isStayLoggedIn() {
        return this.stayLoggedIn;
    }

    private boolean _isTouchtableInterface() {
        return getProperty(SettingsType.TOUCHTABLE_INTERFACE, Boolean.class);
    }

    private boolean _isVSync() {
        return getProperty(SettingsType.V_SYNC, Boolean.class);
    }

    private void _setAmountOfConnections(int noGames) {
        setProperty(SettingsType.SERVER_AMOUNT_OF_CONNECTIONS, noGames);
    }

    private void _setApplicationType(Network.AppType type) {
        this.applicationType = type;
    }

    private void _setClientToken(int connectionID, String clientToken) {
        setProperty(SettingsType.CLIENT_TOKEN, connectionID, clientToken);
    }

    private void _setConditionsChecksum(Conditions conditions, String eulaChecksum) {
        setProperty(SettingsType.CONDITIONS_CHECKSUM, eulaChecksum);
        setProperty(SettingsType.CONDITIONS_REGION, conditions.toString());
    }

    private void _setHardwareScore(SettingsType type, String data) {
        setProperty(type, data);
    }

    private void _setLanguage(TLanguage language) {
        setProperty(SettingsType.TLANGUAGE, language);
    }

    private void _setLastEditorTCurrency(TCurrency currency) {
        setProperty(SettingsType.LAST_EDITOR_TCURRENCY, currency);
    }

    private void _setLastEditorTLanguage(TLanguage language) {
        setProperty(SettingsType.LAST_EDITOR_TLANGUAGE, language);
    }

    private void _setLastEditorUnitSystemType(UnitSystemType unitSystemType) {
        setProperty(SettingsType.LAST_EDITOR_UNIT_SYSTEM_TYPE, unitSystemType);
    }

    private void _setLogToFile(boolean logToFile) {

        setProperty(SettingsType.LOGTOFILE, logToFile);
        TLogger.setLogToFile(logToFile);

    }

    private void _setOculusMode(boolean oculus) {
        this.oculusMode = oculus;
    }

    private void _setOnlineAccountDomain(String domain) {
        setProperty(SettingsType.ONLINE_ACCOUNT_DOMAIN, domain);
    }

    /**
     * Password is stored in base64, this is not safe!
     * @param passwd
     */
    private void _setOnlinePassword(String passwd) {
        try {
            setProperty(SettingsType.ONLINE_PASSWORD, new String(Base64.encode(passwd.getBytes())));
        } catch (Exception e) {
        }
    }

    private void _setOnlineUserName(String username) {
        setProperty(SettingsType.ONLINE_USERNAME, username);
    }

    private void _setPreloaderSkippedVersion(int version) {
        setProperty(SettingsType.PRELOADER_VERSION_SKIPPED, version);
    }

    protected void _setProjectName(String projectName) {
        this.projectName = projectName;
    }

    private void _setRenderer(String renderer) {
        setProperty(SettingsType.RENDERER, renderer);
    }

    protected void _setRunMode(RunMode level) {

        setProperty(SettingsType.RUNMODE, level);
    }

    private void _setScreenActualAmountModels(int drawDistance) {
        setProperty(SettingsType.SCREEN_ACTUAL_AMOUNT_MODELS, drawDistance);
    }

    protected void _setScreenAntiAliasing(int antiAliasing) {

        setProperty(SettingsType.SCREEN_ANTI_ALIASING, antiAliasing);

    }

    protected void _setScreenBloom(int bloom) {
        setProperty(SettingsType.SCREEN_BLOOM, bloom);
    }

    protected void _setScreenCartoon(int cartoon) {
        setProperty(SettingsType.SCREEN_CARTOON, cartoon);
    }

    protected void _setScreenDoF(int bloom) {
        setProperty(SettingsType.SCREEN_DOF, bloom);
    }

    private void _setScreenHDR(int hdr) {
        setProperty(SettingsType.SCREEN_HDR, hdr);
    }

    private void _setScreenHeight(int height) {
        screenHeight = height;
    }

    private void _setScreenMinAmountModels(int drawDistance) {
        setProperty(SettingsType.SCREEN_MIN_AMOUNT_MODELS, drawDistance);
    }

    private void _setScreenScattering(int scatter) {
        setProperty(SettingsType.SCREEN_SCATTERING, scatter);
    }

    private void _setScreenShadows(int shadows) {
        setProperty(SettingsType.SCREEN_SHADOWS, shadows);
    }

    private void _setScreenSSAO(int bloom) {
        setProperty(SettingsType.SCREEN_SSAO, bloom);
    }

    private void _setScreenTextureSize(TextureSize textureQuality) {
        setProperty(SettingsType.SCREEN_TEXTURE_SIZE, textureQuality);
    }

    private void _setScreenWater(int waterQuality) {
        setProperty(SettingsType.SCREEN_WATER, waterQuality);
    }

    private void _setScreenWidth(int width) {
        screenWidth = width;
    }

    /**
     * _set the IP Address for the server
     *
     * @param serverIP
     */
    private void _setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    private void _setServerKey(String key) {
        setProperty(SettingsType.SERVER_KEY, key);
    }

    /**
     * _set the game ID for the server
     *
     * @param gameID
     */
    private void _setServerSlotID(int connectionID, Integer gameID) {
        setProperty(SettingsType.SERVER_SLOT_ID, connectionID, gameID);
    }

    private void _setServerToken(int connectionID, String serverToken) {
        setProperty(SettingsType.SERVER_TOKEN, connectionID, serverToken);
    }

    private void _setShowTutorial(boolean showTutorial) {
        setProperty(SettingsType.SHOW_INTRO_TUTORIAL, showTutorial);
    }

    private void _setSoundVolumePercentage(Float percentage) {
        setProperty(SettingsType.SOUND_PERCENTAGE, percentage);
    }

    private void _setStayLoggedIn(boolean stayLoggedIn) {
        this.stayLoggedIn = stayLoggedIn;
    }

    private void _setTitle(String title) {
        setProperty(SettingsType.TITLE, title);
    }

    private void _setTouchtableInterface(boolean touch) {
        setProperty(SettingsType.TOUCHTABLE_INTERFACE, touch);
    }

    private void _setVideoDirectory(String path) {
        setProperty(SettingsType.VIDEO_DIRECTORY, path);
    }

    private void _setVSync(boolean vsync) {
        setProperty(SettingsType.V_SYNC, vsync);
    }

    private void _stopSlotKeeper() {
        if (settingsActive != null) {
            settingsActive.active = false;
        }
    }

    private boolean containsProperty(SettingsType type, int id) {
        String key = convertToAppSettingsKey(type, id);
        return storage.containsKey(key);
    }

    private String convertToAppSettingsKey(SettingsType type, int id) {

        String key = type.toString().toLowerCase();
        StringTokenizer tokenizer = new StringTokenizer(key, "_");
        key = StringUtils.EMPTY;
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            token = token.substring(0, 1).toUpperCase() + token.substring(1);
            key += token;
        }
        if (id >= 0) {
            key += "-" + id;
        }
        return key;
    }

    private void firstTimeInit() {

        int i = 0;
        while (true) {
            Boolean slotActive = this.isSettingsSlotActive(i);
            if (slotActive == null) {
                // new slot, load from defaults
                break;
            } else if (!slotActive) {
                // re-use old with old settings e.g. tokens
                break;
            }
            // continue search
            i++;
        }
        this.storageID = ROOT_LOCATION + "/" + i;
        this.instanceID = i;
        this.load(storageID);

        // save every sec
        settingsActive = new SettingsActive(storageID);
        settingsActive.start();

        TLogger.info("SettingsManager active in slot: " + i);
    }

    private <T> T getProperty(SettingsType type, Class<T> classz) {
        return getProperty(type, -1, classz);

    }

    @SuppressWarnings("unchecked")
    private <T> T getProperty(SettingsType type, int id, Class<T> classz) {

        // _get properties
        String key = convertToAppSettingsKey(type, id);
        Object result = getProperty(key, classz);
        if (result == null) {
            TLogger.info("No Setting for " + key + " (" + key + "), returning default value.");
            // save first
            this.setProperty(type, id, type.getDefaultValue());
            // return default
            result = type.getDefaultValue();
        }
        return (T) result;

    }

    @SuppressWarnings("unchecked")
    private <T> T getProperty(String key, Class<T> classz) {
        Object result = storage.get(key);

        if (result == null) {
            return null;
        }

        if (classz == null) {
            TLogger.warning("No Setting class defined for " + key + ".");
            return (T) result;
        }

        if (classz.equals(Integer.class)) {
            try {
                return (T) Integer.valueOf(StringUtils.EMPTY + result);
            } catch (Exception exp) {
                TLogger.severe("Value: " + result + " for _setting type " + key + " is not a " + classz.getSimpleName() + ".");
                return (T) Integer.valueOf(Item.NONE);
            }
        }

        else if (!result.getClass().equals(classz)) {
            // Attempt to 'value of'
            try {
                Method method = classz.getMethod("valueOf", String.class);
                result = method.invoke(null, result.toString());
            } catch (Exception e) {
                TLogger.severe("Could not convert settings [" + key + "] into an Object of type [" + classz.getSimpleName() + "]");
            }

        }
        return (T) result;
    }

    protected boolean isReleaseAllowed() {
        try {
            // TODO: Maxim test if this directory is available, if so probably eclipse run project. Not perfect but should work in most
            // cases and does not give false negatives! Using the classpath does not work since applets have directories in there path...
            File file = new File("../engine_assets/Stream/");
            return !file.exists();
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return true;
    }

    private Boolean isSettingsSlotActive(int id) {
        try {
            String storageID = ROOT_LOCATION + "/" + id;
            Preferences prefs = Preferences.userRoot().node(storageID);
            if (prefs == null || prefs.keys().length == 0) {
                return null;
            }
            String timestampKey = StringUtils.capitalizeWithUnderScores(SettingsType.TIMESTAMP.toString());
            for (String key : prefs.keys()) {
                if (key.contains(timestampKey)) {
                    String timeStampString = prefs.get(key, "0");
                    long timestamp = Long.valueOf(timeStampString);
                    long delay = System.currentTimeMillis() - timestamp;

                    if (delay < SettingsActive.PERIOD * 2) {
                        // add timed check here
                        return true;
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void load(String storageID) {
        try {
            this.storage.putAll(this.loadStorage(storageID));
            // Check if everything has been set
            if (!containsProperty(SettingsType.WATERMARK, -1)) {
                // load defaults
                for (SettingsType settingsType : SettingsType.values()) {
                    setProperty(settingsType, settingsType.getDefaultValue());
                }
                // save them
                this.save(storageID);
            }
        } catch (BackingStoreException e) {
            TLogger.exception(e, "Error loading from Registry.");
        }
    }

    private Map<String, Object> loadStorage(String preferencesKey) throws BackingStoreException {

        Preferences prefs = Preferences.userRoot().node(preferencesKey);
        String[] keys = prefs.keys();
        Map<String, Object> storage = new HashMap<>();

        if (keys != null) {
            for (String key : keys) {
                switch (key.charAt(0)) {
                    case 'I':
                        storage.put(key.substring(2), prefs.getInt(key, 0));
                        break;
                    case 'F':
                        storage.put(key.substring(2), prefs.getFloat(key, 0f));
                        break;
                    case 'S':
                        storage.put(key.substring(2), prefs.get(key, (String) null));
                        break;
                    case 'B':
                        storage.put(key.substring(2), prefs.getBoolean(key, false));
                        break;
                    default:
                        throw new UnsupportedOperationException("Undefined setting type: " + key.charAt(0));
                }
            }
        }
        return storage;
    }

    private void save(String storageID) {
        try {
            this.saveStorage(storage, storageID);
        } catch (BackingStoreException e) {
            TLogger.exception(e, "Error saving to Registry.");
        }
    }

    private void saveStorage(Map<String, Object> storage, String preferencesKey) throws BackingStoreException {

        Preferences prefs = Preferences.userRoot().node(preferencesKey);
        prefs.clear();

        for (String key : storage.keySet()) {
            Object val = storage.get(key);
            if (val instanceof Integer) {
                prefs.putInt("I_" + key, (Integer) val);
            } else if (val instanceof Float) {
                prefs.putFloat("F_" + key, (Float) val);
            } else if (val instanceof String) {
                prefs.put("S_" + key, (String) val);
            } else if (val instanceof Boolean) {
                prefs.putBoolean("B_" + key, (Boolean) val);
            }
        }
        prefs.sync();
    }

    private boolean setProperty(SettingsType type, int id, Object value) {

        if (type == null) {
            TLogger.severe("Missing _setting type.");
            return false;
        }

        if (value == null) {
            TLogger.warning("Cannot set setting " + type + " to null");
            return false;
        }

        // First
        String key = convertToAppSettingsKey(type, id);
        return setProperty(key, value);
    }

    protected boolean setProperty(SettingsType type, Object value) {
        return setProperty(type, -1, value);
    }

    private boolean setProperty(String key, Object value) {
        // compare based on Strings; (e.g. Integer (new value) and String (from registry) can never be equal!)
        String oldValue = StringUtils.EMPTY + storage.get(key);

        // Enum should use name() instead of toString()
        String newValue;
        if (value instanceof Enum) {
            newValue = ((Enum<?>) value).name();
        } else {
            newValue = StringUtils.EMPTY + value;
        }

        // only update changes
        if (!newValue.equals(oldValue)) {

            // types supported by JME
            if (value instanceof Integer || value instanceof Float || value instanceof Boolean || value instanceof String) {
                // save in properties
                storage.put(key, value);
            } else {
                // other types are converted to a String
                storage.put(key, newValue);
            }

            save(this.storageID);
            // fire event with type and value
            return true;
        }
        return false;
    }
}
