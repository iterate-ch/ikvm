/*
  Copyright (C) 2009 Volker Berlin (i-net software)

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.

  Jeroen Frijters
  jeroen@frijters.net
  
 */
package sun.jdbc.odbc;

import java.sql.*;

import cli.System.Data.*;
import cli.System.Data.Common.*;

/**
 * This JDBC Driver is a wrapper to the ODBC.NET Data Provider
 */
public class JdbcOdbcResultSetMetaData implements ResultSetMetaData{

    private final DbDataReader reader;
    private final DataTable table;


    public JdbcOdbcResultSetMetaData(DbDataReader reader, DataTable table){
        this.reader = reader;
        this.table = table;
    }


    public String getCatalogName(int column){
        return "";
    }


    public String getColumnClassName(int column){
        cli.System.Type type = getDataColumn(column).get_DataType();
        return type.get_FullName();
    }


    public int getColumnCount(){
        return table.get_Columns().get_Count();
    }


    public int getColumnDisplaySize(int column) throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public String getColumnLabel(int column) throws SQLException{
        return getDataColumn(column).get_Caption();
    }


    public String getColumnName(int column) throws SQLException{
        return getDataColumn(column).get_ColumnName();
    }


    public int getColumnType(int column) throws SQLException{
        cli.System.Type type = getDataColumn(column).get_DataType();
        // TODO Auto-generated method stub
        return Types.OTHER;
    }


    public String getColumnTypeName(int column) throws SQLException{
        return reader != null ? reader.GetDataTypeName(column - 1) : "";
    }


    public int getPrecision(int column) throws SQLException{
        return getDataColumn(column).get_MaxLength();
    }


    public int getScale(int column) throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public String getSchemaName(int column) throws SQLException{
        // TODO Auto-generated method stub
        return null;
    }


    public String getTableName(int column) throws SQLException{
        return table.get_TableName();
    }


    public boolean isAutoIncrement(int column) throws SQLException{
        return getDataColumn(column).get_AutoIncrement();
    }


    public boolean isCaseSensitive(int column) throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public boolean isCurrency(int column) throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public boolean isDefinitelyWritable(int column) throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public int isNullable(int column) throws SQLException{
        return getDataColumn(column).get_AllowDBNull() ? columnNullable : columnNoNulls;
    }


    public boolean isReadOnly(int column) throws SQLException{
        return getDataColumn(column).get_ReadOnly();
    }


    public boolean isSearchable(int column) throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public boolean isSigned(int column) throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public boolean isWritable(int column) throws SQLException{
        return !getDataColumn(column).get_ReadOnly();
    }


    public boolean isWrapperFor(Class<?> iface){
        return iface.isAssignableFrom(this.getClass());
    }


    public <T>T unwrap(Class<T> iface) throws SQLException{
        if(isWrapperFor(iface)){
            return (T)this;
        }
        throw new SQLException(this.getClass().getName() + " does not implements " + iface.getName() + ".", "01000");
    }


    private DataColumn getDataColumn(int column){
        return table.get_Columns().get_Item(column - 1);
    }
}
