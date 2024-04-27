import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;

import javax.management.ObjectName;
import java.beans.PropertyChangeListener;
import java.io.File;

public class socket implements Container {
    @Override
    public Log getLogger() {
        return null;
    }

    @Override
    public String getLogName() {
        return "";
    }

    @Override
    public ObjectName getObjectName() {
        return null;
    }

    @Override
    public String getDomain() {
        return "";
    }

    @Override
    public String getMBeanKeyProperties() {
        return "";
    }

    @Override
    public Pipeline getPipeline() {
        return null;
    }

    @Override
    public Cluster getCluster() {
        return null;
    }

    @Override
    public void setCluster(Cluster cluster) {

    }

    @Override
    public int getBackgroundProcessorDelay() {
        return 0;
    }

    @Override
    public void setBackgroundProcessorDelay(int i) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public Container getParent() {
        return null;
    }

    @Override
    public void setParent(Container container) {

    }

    @Override
    public ClassLoader getParentClassLoader() {
        return null;
    }

    @Override
    public void setParentClassLoader(ClassLoader classLoader) {

    }

    @Override
    public Realm getRealm() {
        return null;
    }

    @Override
    public void setRealm(Realm realm) {

    }

    @Override
    public void backgroundProcess() {

    }

    @Override
    public void addChild(Container container) {

    }

    @Override
    public void addContainerListener(ContainerListener containerListener) {

    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public Container findChild(String s) {
        return null;
    }

    @Override
    public Container[] findChildren() {
        return new Container[0];
    }

    @Override
    public ContainerListener[] findContainerListeners() {
        return new ContainerListener[0];
    }

    @Override
    public void removeChild(Container container) {

    }

    @Override
    public void removeContainerListener(ContainerListener containerListener) {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void fireContainerEvent(String s, Object o) {

    }

    @Override
    public void logAccess(Request request, Response response, long l, boolean b) {

    }

    @Override
    public AccessLog getAccessLog() {
        return null;
    }

    @Override
    public int getStartStopThreads() {
        return 0;
    }

    @Override
    public void setStartStopThreads(int i) {

    }

    @Override
    public File getCatalinaBase() {
        return null;
    }

    @Override
    public File getCatalinaHome() {
        return null;
    }

    @Override
    public void addLifecycleListener(LifecycleListener lifecycleListener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener lifecycleListener) {

    }

    @Override
    public void init() throws LifecycleException {

    }

    @Override
    public void start() throws LifecycleException {

    }

    @Override
    public void stop() throws LifecycleException {

    }

    @Override
    public void destroy() throws LifecycleException {

    }

    @Override
    public LifecycleState getState() {
        return null;
    }

    @Override
    public String getStateName() {
        return "";
    }
}
