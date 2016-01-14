/*
This file is part of Intake24.

Copyright 2015, 2016 Newcastle University.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package net.scran24.user.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.scran24.datastore.DataStore;
import uk.ac.ncl.openlab.intake24.services.AutoReloadIndex;
import uk.ac.ncl.openlab.intake24.services.FoodDataService;
import uk.ac.ncl.openlab.intake24.services.foodindex.AbstractFoodIndex;
import uk.ac.ncl.openlab.intake24.services.foodindex.FoodIndex;
import uk.ac.ncl.openlab.intake24.services.foodindex.Splitter;
import uk.ac.ncl.openlab.intake24.services.foodindex.english.FoodIndexImpl_en_GB;
import uk.ac.ncl.openlab.intake24.services.foodindex.english.SplitterImpl_en_GB;
import uk.ac.ncl.openlab.intake24.services.nutrition.NutrientMappingService;

import org.workcraft.gwt.shared.client.Pair;

import scala.concurrent.duration.Duration;
import scala.runtime.AbstractFunction0;
import uk.ac.ncl.openlab.intake24.datastoresql.DataStoreJavaAdapter;
import uk.ac.ncl.openlab.intake24.datastoresql.DataStoreScala;
import uk.ac.ncl.openlab.intake24.datastoresql.DataStoreSqlImpl;
import uk.ac.ncl.openlab.intake24.foodsql.FoodDataServiceSqlImpl;
import uk.ac.ncl.openlab.intake24.nutrientsndns.NdnsNutrientMappingServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SqlConfig extends AbstractModule {
	public final Map<String, String> webXmlConfig;

	public SqlConfig(Map<String, String> webXmlConfig) {
		this.webXmlConfig = webXmlConfig;
	}

	@Provides
	@Singleton
	protected Map<String, FoodIndex> localFoodIndex(Injector injector) {
		final FoodDataService foodDataService = injector.getInstance(FoodDataService.class);
		Map<String, FoodIndex> result = new HashMap<String, FoodIndex>();

		result.put("en_GB", new AutoReloadIndex(new AbstractFunction0<AbstractFoodIndex>() {
			@Override
			public AbstractFoodIndex apply() {
				return new FoodIndexImpl_en_GB(foodDataService);
			}
		}, Duration.create(60, TimeUnit.MINUTES), Duration.create(60, TimeUnit.MINUTES), "English"));

		result.put("ar_AE", new AutoReloadIndex(new AbstractFunction0<AbstractFoodIndex>() {
			@Override
			public AbstractFoodIndex apply() {
				return new FoodIndexImpl_en_GB(foodDataService);
			}
		}, Duration.create(30, TimeUnit.MINUTES), Duration.create(60, TimeUnit.MINUTES), "Arabic"));

		return result;
	}

	@Provides
	@Singleton
	protected Map<String, Splitter> localSplitter(Injector injector) {
		FoodDataService foodDataService = injector.getInstance(FoodDataService.class);

		Map<String, Splitter> result = new HashMap<String, Splitter>();
		result.put("en_GB", new SplitterImpl_en_GB(foodDataService));
		return result;
	}

	@Provides
	@Singleton
	protected List<Pair<String, ? extends NutrientMappingService>> nutrientTables(Injector injector) {
		FoodDataService foodDataService = injector.getInstance(FoodDataService.class);
		List<Pair<String, ? extends NutrientMappingService>> result = new ArrayList<Pair<String, ? extends NutrientMappingService>>();
		result.add(Pair.create("NDNS", new NdnsNutrientMappingServiceImpl(webXmlConfig.get("ndns-data-path"), foodDataService)));
		return result;
	}

	@Provides
	@Singleton
	protected DataStoreScala dataStoreSqlImpl(Injector injector) {
		HikariConfig cpConfig = new HikariConfig();
		cpConfig.setJdbcUrl(webXmlConfig.get("sql-system-db-url"));
		cpConfig.setUsername(webXmlConfig.get("sql-system-db-user"));
		cpConfig.setPassword(webXmlConfig.get("sql-system-db-password"));

		return new DataStoreSqlImpl(new HikariDataSource(cpConfig));
	}

	@Provides
	@Singleton
	protected FoodDataService foodDataServiceSqlImpl(Injector injector) {
		HikariConfig cpConfig = new HikariConfig();
		cpConfig.setJdbcUrl(webXmlConfig.get("sql-foods-db-url"));
		cpConfig.setUsername(webXmlConfig.get("sql-foods-db-user"));
		cpConfig.setPassword(webXmlConfig.get("sql-foods-db-password"));

		return new FoodDataServiceSqlImpl(new HikariDataSource(cpConfig));
	}

	@Override
	protected void configure() {
		bind(DataStore.class).to(DataStoreJavaAdapter.class);
	}

}