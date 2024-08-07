/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.flink.action;

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.operation.OrphanFilesClean;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;

import static org.apache.paimon.operation.OrphanFilesClean.executeOrphanFilesClean;

/** Action to remove the orphan data files and metadata files. */
public class RemoveOrphanFilesAction extends ActionBase {

    private final List<OrphanFilesClean> tableCleans;

    public RemoveOrphanFilesAction(
            String warehouse,
            String databaseName,
            @Nullable String tableName,
            Map<String, String> catalogConfig)
            throws Catalog.TableNotExistException, Catalog.DatabaseNotExistException {
        super(warehouse, catalogConfig);
        this.tableCleans =
                OrphanFilesClean.createOrphanFilesCleans(catalog, databaseName, tableName);
    }

    public void olderThan(String olderThan) {
        tableCleans.forEach(clean -> clean.olderThan(olderThan));
    }

    public void dryRun() {
        tableCleans.forEach(clean -> clean.fileCleaner(path -> {}));
    }

    @Override
    public void run() throws Exception {
        executeOrphanFilesClean(tableCleans);
    }
}
