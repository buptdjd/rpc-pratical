package rpc.ipc.client.connection;

import rpc.ipc.util.RPCClientException;

public interface ConnectionPool {
    public Connection getConnection() throws RPCClientException;

    public void releaseConnection(Connection conn) throws RPCClientException;

    public void closeRoughly() throws RPCClientException;

    public void closeSoftly() throws RPCClientException;

    /**
     * �Ƿ��Ѿ�����ȫ���ͷ�.
     * @return
     */
    public boolean isCloseComplemented();
}
