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

package uk.ac.ncl.openlab.intake24.services

import net.scran24.fooddef.SplitList
import net.scran24.fooddef.UserCategoryHeader
import net.scran24.fooddef.UserFoodHeader

trait IndexFoodDataService {
  def indexableCategories(locale: String): Seq[UserCategoryHeader]
  def indexableFoods(locale: String): Seq[UserFoodHeader]
  def synsets(locale: String): Seq[Set[String]]
  def splitList(locale: String): SplitList
}