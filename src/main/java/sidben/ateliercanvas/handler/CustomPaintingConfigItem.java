package sidben.ateliercanvas.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.StringUtils;
import sidben.ateliercanvas.reference.TextFormatTable;


/**
 * The object represents all config info about one Custom Painting.
 * 
 * @author sidben
 * 
 */
public class CustomPaintingConfigItem
{

    private final static int       EXPECTED_LENGTH  = 8;

    private final SimpleDateFormat sdfSave          = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat sdfDisplay       = new SimpleDateFormat(ConfigurationHandler.paintingDateFormat);
    private final String           ancientDate      = "1700-01-01";

    private String                 _fileName        = "";
    private String                 _uuid            = "";
    private boolean                _enabled         = false;
    private long                   _sizeBytes       = -1;
    private String                 _title           = "";
    private String                 _author          = "";
    private Date                   _creationDate    = new Date();
    private Date                   _lastUpdateDate  = new Date();
    private int                    _widthTile       = 1;                    // TODO: gather this info when importing paintings
    private int                    _heightTile      = 1;                    // TODO: update this info on the manager, if don't match with actual picture

    private String                 validationErrors = "";



    
    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------
    
    /**
     * Creates a new config info object.
     * 
     * 
     * @param value
     *            Information about the custom painting. It's expected the following format:
     * 
     *            <ul>
     *            <li><b>[0]</b> - Image file name. String, required.</li>
     *            <li><b>[1]</b> - Painting UUID. Required (created automatically by the mod).</li>
     *            <li><b>[2]</b> - Painting enabled. Boolean, required.</li>
     *            <li><b>[3]</b> - File size, in bytes. Float, required.</li>
     *            <li><b>[4]</b> - Painting name. String, optional.</li>
     *            <li><b>[5]</b> - Painting author. String, optional.</li>
     *            <li><b>[6]</b> - Creation date. Date (format: YYYY-MM-DD), required.</li>
     *            <li><b>[7]</b> - Last update date. Date (format: YYYY-MM-DD), required. Initially, will be the same as the creation date.</li>
     *            <li><b>[8]</b> - Tile width. Integer, optional.</li>
     *            <li><b>[9]</b> - Tile height. Integer, optional.</li>
     * 
     *            <li><b>[?]</b> - Player name that imported / created / uploaded the painting.</li>
     *            <li><b>[?]</b> - Player UUID that imported / created / uploaded the painting.</li>
     *            <li><b>[?]</b> - Painting lore. String, optional.</li>
     *            </ul>
     * 
     */
    public CustomPaintingConfigItem(String[] value) {
        final String[] _entryData = value;

        // Parse the value array
        if (this.isValidArray(_entryData)) {
            final Calendar cal = Calendar.getInstance();
            cal.set(1700, 0, 1);
            final Date ancientTimes = cal.getTime();


            this._fileName = _entryData[0];
            this._uuid = _entryData[1];
            this._enabled = parseBoolean(_entryData[2]);
            this._sizeBytes = parseLongWithDefault(_entryData[3], -1);
            this._title = _entryData[4];
            this._author = _entryData[5];
            this._creationDate = parseDateWithDefault(_entryData[6], ancientTimes);
            this._lastUpdateDate = parseDateWithDefault(_entryData[7], ancientTimes);
            
            // Reads optional parameters
            if (_entryData.length >= 9) {
                this._widthTile = parseIntWithDefault(_entryData[8], 1, 1);
                this._heightTile = parseIntWithDefault(_entryData[9], 1, 1);
            }

        }
    }


    /**
     * Creates a new config info object.
     * 
     * @param fileName
     *            Name of the painting image file.
     * @param uuid
     *            Unique identifier.
     * @param enabled
     *            Is the painting enabled in-game?
     * @param fileSize
     *            Size of the painting file, in bytes.
     * @param title
     *            Name of the painting.
     * @param author
     *            Author of the painting.
     * @param createDate
     *            Date when the painting was added to the mod.
     * @param updateDate
     *            Last time the painting config was updated.
     */
    public CustomPaintingConfigItem(String fileName, String uuid, boolean enabled, long fileSize, String title, String author, Date createDate, Date updateDate) {
        this._author = author;
        this._creationDate = createDate;
        this._enabled = enabled;
        this._fileName = fileName;
        this._uuid = uuid;
        this._lastUpdateDate = updateDate;
        this._sizeBytes = fileSize;
        this._title = title;
    }


    /**
     * Creates a new config info object. The unique identifier receives a new random value and the
     * create and last update dates are defined as the current date.
     * 
     * @param fileName
     *            Name of the painting image file.
     * @param enabled
     *            Is the painting enabled in-game?
     * @param fileSize
     *            Size of the painting file, in bytes.
     * @param title
     *            Name of the painting.
     * @param author
     *            Author of the painting.
     */
    public CustomPaintingConfigItem(String fileName, boolean enabled, long fileSize, String title, String author) {
        this(fileName, UUID.randomUUID().toString(), enabled, fileSize, title, author, new Date(), new Date());
    }

    
    
    
   
    //----------------------------------------------------------------
    // Properties
    //----------------------------------------------------------------

    public String getPaintingFileName()
    {
        return this._fileName;
    }

    public String getUUID()
    {
        return this._uuid;
    }

    public boolean getIsEnabled()
    {
        return this._enabled;
    }

    public long getExpectedSize()
    {
        return this._sizeBytes;
    }

    /** Returns the painting title. If empty, will return a default text. */
    public String getPaintingTitle()
    {
        return this._title.isEmpty() ? TextFormatTable.ITALIC + StatCollector.translateToLocal(this.getLanguageKey("title_empty")) + TextFormatTable.RESET : this._title;
    }

    /** Returns the painting title, even if empty */
    public String getPaintingTitleRaw()
    {
        return this._title;
    }

    /** Returns the painting author. If empty, will return a default text. */
    public String getPaintingAuthor()
    {
        return this._author.isEmpty() ? TextFormatTable.ITALIC + StatCollector.translateToLocal(this.getLanguageKey("author_empty")) + TextFormatTable.RESET : this._author;
    }

    /** Returns the painting author, even if empty */
    public String getPaintingAuthorRaw()
    {
        return this._author;
    }

    public Date getCreationDate()
    {
        return this._creationDate;
    }

    public Date getLastUpdateDate()
    {
        return this._lastUpdateDate;
    }

    public String getFormatedCreationDate()
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(this.getCreationDate());

        if (cal.get(Calendar.YEAR) > 1700) {
            return this.sdfDisplay.format(this.getCreationDate());
        } else {
            return "-";
        }
    }

    public String getFormatedLastUpdateDate()
    {
        if (this.getLastUpdateDate().equals(this.getCreationDate())) {
            return "-";
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTime(this.getLastUpdateDate());

        if (cal.get(Calendar.YEAR) > 1700) {
            return this.sdfDisplay.format(this.getCreationDate());
        } else {
            return "-";
        }
    }
    
    /** Returns the painting width in tiles. Each 'tile' is a 16x16 block, so a painting with 48x32 size in pixels would occupy 3x2 tiles */
    public int getTileWidth()
    {
        return this._widthTile;
    }

    /** Returns the painting height in tiles. Each 'tile' is a 16x16 block, so a painting with 48x32 size in pixels would occupy 3x2 tiles */
    public int getTileHeight()
    {
        return this._heightTile;
    }

    


    public void setPaintingTitle(String value)
    {
        this._title = value;
    }

    public void setPaintingAuthor(String value)
    {
        this._author = value;
    }

    public void setIsEnabled(boolean value)
    {
        this._enabled = value;
    }

    public void setSize(int tileWidth, int tileHeight)
    {
        this._widthTile = Math.max(tileWidth, 1);
        this._heightTile = Math.max(tileHeight, 1);
    }

    public void setSizePixels(int imageWidth, int imageHeight)
    {
        this._widthTile = (int) Math.max((imageWidth / 16.0), 1);
        this._heightTile = (int) Math.max((imageHeight / 16.0), 1);
    }


    
    
    //----------------------------------------------------------------
    // Information about this entry integrity
    //----------------------------------------------------------------

    /**
     * @return TRUE if this object has the minimum expected values to be used.
     */
    public boolean isValid()
    {
        if (!this.validationErrors.isEmpty()) {
            // There are previous validation errors
            return false;
        }
        if (this._fileName.isEmpty()) {
            validationErrors = StatCollector.translateToLocal(this.getLanguageKey("error_empty_filename"));
            return false;
        }
        if (this._uuid.isEmpty()) {
            validationErrors = StatCollector.translateToLocal(this.getLanguageKey("error_empty_uuid"));
            return false;
        }
        if (this._sizeBytes <= 0) {
            validationErrors = StatCollector.translateToLocal(this.getLanguageKey("error_invalid_filesize"));
            return false;
        }

        return true;
    }


    public String getValiadtionErrors()
    {
        return this.validationErrors;
    }



    public String[] ToStringArray()
    {
        String dateCreated = sdfSave.format(this._creationDate);
        String dateUpdated = sdfSave.format(this._lastUpdateDate);

        if (dateCreated.equals(ancientDate)) {
            dateCreated = "";
        }
        if (dateUpdated.equals(ancientDate)) {
            dateUpdated = "";
        }


        return new String[] { this._fileName, this._uuid, (this._enabled ? "1" : "0"), Long.toString(this._sizeBytes), this._title, this._author, dateCreated, dateUpdated, Integer.toString(this._widthTile), Integer.toString(this._heightTile) };
    }


    @Override
    public String toString()
    {
        return StringUtils.join(this.ToStringArray(), "|");
    }
    
    
    /**
     * Returns how the config array is expected to behave.
     */
    public static String getArrayDescription()
    {
        String r = "";

        r += "Each array entry is expected to have the following format.\n";
        r += "All fields are required, even if blank. Fields with [*] are an exception and can't be empty\n\n";
        r += "    File name [*] (only PNG files are accepted)\n";
        r += "    UUID (each entry must have a unique value. leave blank and the mod will create a new UUID)\n";
        r += "    Enabled (1 or 0) [*]\n";
        r += "    File size, in bytes (only numbers) [*]\n";
        r += "    Painting title\n";
        r += "    Author's name\n";
        r += "    Creation date (format yyyy-MM-dd)\n";
        r += "    Last update date (format yyyy-MM-dd)\n";

        return r;
    }

    
    
    
    
    

    //----------------------------------------------------------------
    // Generic helpers
    //----------------------------------------------------------------

    /**
     * @return TRUE if the entryData array has the minimum expected size.
     */
    private boolean isValidArray(String[] entryData)
    {
        if (entryData == null) {
            validationErrors = "Null config string array";
            return false;
        } else if (entryData.length < EXPECTED_LENGTH) {
            validationErrors = "Config array shorter than expected";
            return false;
        } else {
            return true;
        }
    }


    /**
     * Returns the full language key for elements of this GUI.
     */
    protected String getLanguageKey(String name)
    {
        return "sidben.ateliercanvas.config.painting_info." + name;
    }


    
    
    

    
    //----------------------------------------------------------------
    // Parser helpers
    //----------------------------------------------------------------
    private int parseIntWithDefault(String textValue, int defaultValue, int minimumValue)
    {
        try {
            return Math.max(Integer.parseInt(textValue.trim()), minimumValue);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private long parseLongWithDefault(String textValue, long defaultValue)
    {
        try {
            return Long.parseLong(textValue.trim());
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    private Date parseDateWithDefault(String textValue, Date defaultValue)
    {
        try {
            return sdfSave.parse(textValue);
        } catch (final ParseException e) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(String textValue) {
        return textValue.equals("1");
    }

    
}
