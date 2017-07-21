/**
 * This package contains all communication to external systems. Those systems
 * are:
 * <ul>
 * <li>ElasticSearch for Indexing via REST
 * {@link org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary
 * ElasticsearchBoundary}</li>
 * <li>MDM for querying the dataitems and getting notifications
 * {@link org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary
 * MDMBoundary}</li>
 * <li>JEE Container for creating the initial index
 * {@link org.eclipse.mdm.freetextindexer.boundary.StartupBoundary
 * StartupBoundary}</li>
 * </ul>
 * 
 */
package org.eclipse.mdm.freetextindexer.boundary;
