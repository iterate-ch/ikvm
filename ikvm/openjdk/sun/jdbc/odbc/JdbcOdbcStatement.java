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

import cli.System.Data.Common.*;

/**
 * This JDBC Driver is a wrapper to the ODBC.NET Data Provider
 */
public class JdbcOdbcStatement implements Statement{

    private final JdbcOdbcConnection jdbcConn;

    private final DbCommand command;


    public JdbcOdbcStatement(JdbcOdbcConnection jdbcConn, DbCommand command){
        this.jdbcConn = jdbcConn;
        this.command = command;
    }


    public void addBatch(String sql) throws SQLException{
        // TODO Auto-generated method stub

    }


    public void cancel() throws SQLException{
        try{
            command.Cancel();
        }catch(Exception ex){
            throw new SQLException(ex);
        }
    }


    public void clearBatch() throws SQLException{
        // TODO Auto-generated method stub

    }


    public void clearWarnings() throws SQLException{
        // TODO Auto-generated method stub

    }


    public void close(){
        command.Dispose();
    }


    public boolean execute(String sql) throws SQLException{
        try{
            command.set_CommandText(sql);
            DbDataReader reader = command.ExecuteReader();
            return reader != null;
        }catch(Exception ex){
            throw new SQLException(ex);
        }
    }


    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException{
        throw new UnsupportedOperationException();
    }


    public boolean execute(String sql, int[] columnIndexes) throws SQLException{
        throw new UnsupportedOperationException();
    }


    public boolean execute(String sql, String[] columnNames) throws SQLException{
        throw new UnsupportedOperationException();
    }


    public int[] executeBatch() throws SQLException{
        // TODO Auto-generated method stub
        return null;
    }


    public ResultSet executeQuery(String sql) throws SQLException{
        try{
            command.set_CommandText(sql);
            return new JdbcOdbcResultSet(this, command.ExecuteReader());
        }catch(Exception ex){
            throw new SQLException(ex);
        }
    }


    public int executeUpdate(String sql) throws SQLException{
        try{
            command.set_CommandText(sql);
            return command.ExecuteNonQuery();
        }catch(Exception ex){
            throw new SQLException(ex);
        }
    }


    public int executeUpdate(String sql, int autoGeneratedKeys){
        throw new UnsupportedOperationException();
    }


    public int executeUpdate(String sql, int[] columnIndexes){
        throw new UnsupportedOperationException();
    }


    public int executeUpdate(String sql, String[] columnNames){
        throw new UnsupportedOperationException();
    }


    public Connection getConnection(){
        return jdbcConn;
    }


    public int getFetchDirection() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public int getFetchSize() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public ResultSet getGeneratedKeys(){
        throw new UnsupportedOperationException();
    }


    public int getMaxFieldSize() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public int getMaxRows() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public boolean getMoreResults() throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public boolean getMoreResults(int current) throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public int getQueryTimeout() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public ResultSet getResultSet() throws SQLException{
        // TODO Auto-generated method stub
        return null;
    }


    public int getResultSetConcurrency() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public int getResultSetHoldability() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public int getResultSetType() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public int getUpdateCount() throws SQLException{
        // TODO Auto-generated method stub
        return 0;
    }


    public SQLWarning getWarnings() throws SQLException{
        // TODO Auto-generated method stub
        return null;
    }


    public boolean isClosed() throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public void setCursorName(String name) throws SQLException{
        // TODO Auto-generated method stub

    }


    public void setEscapeProcessing(boolean enable) throws SQLException{
        // TODO Auto-generated method stub

    }


    public void setFetchDirection(int direction) throws SQLException{
        // TODO Auto-generated method stub

    }


    public void setFetchSize(int rows) throws SQLException{
        // TODO Auto-generated method stub

    }


    public void setMaxFieldSize(int max) throws SQLException{
        // TODO Auto-generated method stub

    }


    public void setMaxRows(int max) throws SQLException{
        // TODO Auto-generated method stub

    }


    public boolean isPoolable() throws SQLException{
        // TODO Auto-generated method stub
        return false;
    }


    public void setPoolable(boolean poolable) throws SQLException{
        // TODO Auto-generated method stub

    }


    public void setQueryTimeout(int seconds) throws SQLException{
        // TODO Auto-generated method stub

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

}
