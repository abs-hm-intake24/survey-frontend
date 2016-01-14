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

import net.scran24.datastore.DataStore;
import net.scran24.datastore.DataStoreException;
import net.scran24.datastore.mongodb.MongoDbDataStore;

import org.workcraft.gwt.shared.client.Pair;

import uk.ac.ncl.openlab.intake24.foodxml.FoodDataServiceXmlImpl;
import uk.ac.ncl.openlab.intake24.nutrientsndns.NdnsNutrientMappingServiceImpl;
import uk.ac.ncl.openlab.intake24.services.FoodDataService;
import uk.ac.ncl.openlab.intake24.services.foodindex.FoodIndex;
import uk.ac.ncl.openlab.intake24.services.foodindex.Splitter;
import uk.ac.ncl.openlab.intake24.services.foodindex.english.FoodIndexImpl_en_GB;
import uk.ac.ncl.openlab.intake24.services.foodindex.english.SplitterImpl_en_GB;
import uk.ac.ncl.openlab.intake24.services.nutrition.NutrientMappingService;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class MongoDBAndXmlConfig extends AbstractModule {
	public final Map<String, String> webXmlConfig;

	public MongoDBAndXmlConfig(Map<String, String> webXmlConfig) {
		this.webXmlConfig = webXmlConfig;
	}

	@Provides
	@Singleton
	protected Map<String, FoodIndex> localFoodIndex(Injector injector) {
		FoodDataService foodDataService = injector.getInstance(FoodDataService.class);

		Map<String, FoodIndex> result = new HashMap<String, FoodIndex>();
		result.put("en_GB", new FoodIndexImpl_en_GB(foodDataService));
		result.put("ru_RU", new FoodIndexImpl_en_GB(foodDataService));
		result.put("ar_AE", new FoodIndexImpl_en_GB(foodDataService));
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
	protected DataStore dataStore() {
		try {
			return new MongoDbDataStore(webXmlConfig.get("mongodb-host"), Integer.parseInt(webXmlConfig.get("mongodb-port")),
					webXmlConfig.get("mongodb-database"), webXmlConfig.get("mongodb-user"), webXmlConfig.get("mongodb-password"));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		} catch (DataStoreException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named("xml-data-path")).to(webXmlConfig.get("xml-data-path"));
		bind(FoodDataService.class).to(FoodDataServiceXmlImpl.class);
	}
}