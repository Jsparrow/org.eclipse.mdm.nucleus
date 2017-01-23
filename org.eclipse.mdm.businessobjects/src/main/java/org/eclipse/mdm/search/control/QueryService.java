package org.eclipse.mdm.search.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.base.query.Query;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchParamParser;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.search.entity.Column;
import org.eclipse.mdm.search.entity.EntityId;
import org.eclipse.mdm.search.entity.LoadRequest;
import org.eclipse.mdm.search.entity.QueryRequest;
import org.eclipse.mdm.search.entity.Row;
import org.eclipse.mdm.search.entity.SourceFilter;

import com.google.common.base.Strings;

@Stateless
public class QueryService {
	@EJB
	private ConnectorService connectorService;
	
	public List<Row> queryRows(QueryRequest request) {
		List<Row> rows = new ArrayList<>();

		for (SourceFilter filter : request.getFilters()) {
			rows.addAll(queryRows(filter, request.getColumns()));
		}
		return rows;
	}

	public List<Row> queryRows(SourceFilter filter, List<String> columns) {
		List<Row> rows = new ArrayList<>();
		
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(filter.getSourceName());
			
			ModelManager mm = em.getModelManager().get();
			
			List<Result> result = addSelectRows(mm, mm.createQuery(), columns)
					.fetch(SearchParamParser.parseFilterString(mm.listEntityTypes(), filter.getFilter()));

			for (Result r : result)
			{
				Row row = new Row();
				for (Record record : r) {
					for (Value value : record.getValues().values()) {
						row.addColumn(new Column(
								record.getEntityType().getName(),
								value.getName(), 
								Strings.emptyToNull(Objects.toString(value.extract())), 
								Strings.emptyToNull(value.getUnit())));
					}
				}
				rows.add(row);
			}

			return rows;

		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		} 
	}

	private Query addSelectRows(ModelManager mm, Query query, List<String> columns) {
		for (String c : columns) {
			query.select(getAttribute(mm, c));
		}
		return query;
	}

	private Attribute getAttribute(ModelManager mm, String c) {
		String[] parts = c.split("\\.");
		// TODO check parts
		return mm.getEntityType(parts[0]).getAttribute(parts[1]);
	}

	public List<Row> loadRows(LoadRequest request) {
		Map<String, List<EntityId>> map = request.getEntityIds().stream().collect(Collectors.groupingBy(e -> e.getSource()));
		
		List<Row> rows = new ArrayList<>();

		for (String source : map.keySet()) {
			rows.addAll(loadRows(source, map.get(source), request.getColumns()));
		}
		return rows;
	}
	
	public List<Row> loadRows(String source, List<EntityId> entityIds, List<String> columns) {
		List<Row> rows = new ArrayList<>();
		
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(source);
			ModelManager mm = em.getModelManager().get();
			
			String filter = entityIds.stream().map(e -> toFilterString(mm, e)).collect(Collectors.joining(" or "));
			
			Query query = em.getModelManager().get().createQuery();
			addSelectRows(mm, query, columns);
			
			List<Result> result = query.fetch(SearchParamParser.parseFilterString(mm.listEntityTypes(), filter));

			for (Result r : result)
			{
				Row row = new Row();
				for (Record record : r) {
					for (Value value : record.getValues().values()) {
						row.addColumn(new Column(
								record.getEntityType().getName(),
								value.getName(), 
								Strings.emptyToNull(Objects.toString(value.extract())), 
								Strings.emptyToNull(value.getUnit())));
					}
				}
				rows.add(row);
			}

			return rows;

		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		} 
	}

	private String toFilterString(ModelManager mm, EntityId entityId) {
		EntityType type = mm.getEntityType(entityId.getType());
		return type.getName() + "." + type.getIDAttribute() + " eq " + entityId.getId();
	}
	
}
