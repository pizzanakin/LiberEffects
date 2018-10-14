package net.libercraft.libereffects.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import net.libercraft.libercore.Database;
import net.libercraft.libercore.LiberCore;

public class EffectDatabase extends Database {
	private static Database instance;

	public EffectDatabase(JavaPlugin plugin) {
		super(plugin);
		instance = this;
	}

	@Override
	public List<String> getTables() {
		List<String> tables = new ArrayList<String>();
		tables.add("quality");
		return tables;
	}

	@Override
	public List<String> getColumns(String table) {
    	List<String> columns = new ArrayList<String>();
		switch (table) {
    	case "quality": 
			columns.add("uuid TEXT NOT NULL PRIMARY KEY UNIQUE");
			columns.add("quality INTEGER NOT NULL");
    	break;
    	default:
    		return null;
		}
		return columns;
	}
    
    public static Map<String, Integer> loadQuality() {
    	Map<String, Integer> returnvalue = new HashMap<String, Integer>();
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = instance.getConnection();
    		ps = conn.prepareStatement("SELECT * FROM quality");
    		rs = ps.executeQuery();
    		while (rs.next()) {
    			String uuid = rs.getString("uuid");
    			int quality = rs.getInt("quality");
    			returnvalue.put(uuid, quality);
    		}
    	} catch (SQLException ex) {
        	LiberCore.get().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
            	LiberCore.get().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    	return returnvalue;
    }
    
    public static void saveQuality(String uuid, int quality) {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	
    	try {
    		conn = instance.getConnection();
    		ps = conn.prepareStatement("REPLACE INTO quality(uuid,quality) VALUES (?,?)");
    		
    		ps.setString(1, uuid);
    		ps.setInt(2, quality);
    		ps.executeUpdate();
    	} catch (SQLException ex) {
        	LiberCore.get().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
            	LiberCore.get().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return; 
    }
}
