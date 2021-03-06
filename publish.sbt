/* Copyright 2012-2016 Micronautics Research Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. */

publishArtifact in Test := false

// bintray settings
bintrayOrganization := Some("micronautics")
bintrayPackageLabels := Seq("play")
bintrayRepository := "play"
bintrayVcsUrl := Some(s"git@github.com:mslinn/${ name.value }.git")

// sbt-site settings
// enablePlugins(SiteScaladocPlugin)
// siteSourceDirectory := target.value / "api"
// publishSite

// sbt-ghpages settings
// enablePlugins(GhpagesPlugin)
// git.remoteRepo := s"git@github.com:mslinn/${ name.value }.git"
