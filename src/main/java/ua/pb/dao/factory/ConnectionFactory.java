package ua.pb.dao.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component("connectionFactory")
public class ConnectionFactory extends BasePooledObjectFactory<Connection> {

    @Autowired
    private DataSource dataSource;

    @Override
    public Connection create() throws Exception {
        return dataSource.getConnection();
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<Connection>(connection);
    }

    @Override
    public void destroyObject(PooledObject<Connection> connectionWrapper) throws Exception {
        if (!connectionWrapper.getObject().isClosed()) {
            connectionWrapper.getObject().close();
        }
    }
}
