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

package controllers

import play.api.mvc.Controller
import play.api.libs.json.Json
import play.api.mvc.Action

import net.scran24.fooddef.nutrients.EnergyKcal

import play.api.libs.json.JsError
import scala.concurrent.Future

import javax.inject.Inject
import play.api.i18n.MessagesApi
import com.mohiva.play.silhouette.api.Silhouette
import models.User
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.Environment

class Users @Inject() (val messagesApi: MessagesApi, 
    val env: Environment[User, JWTAuthenticator]) extends Silhouette[User, JWTAuthenticator] {  
  
  def info(survey: String, userName: String) = UserAwareAction.async {
    Future.successful(Ok(""))
  } 
}