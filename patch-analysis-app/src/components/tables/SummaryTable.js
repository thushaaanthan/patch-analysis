/*
Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
WSO2 Inc. licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

import React from 'react';
import Request from 'react-http-request';
import {DataType, SortDirection, TacoTable} from 'react-taco-table';
import '../../styles/react-taco-table.css';
import 'react-tabs/style/react-tabs.css';
import url from '../../configuration';


const columns = [{
    id: 'jira_name',
    type: DataType.String,
    header: 'JIRA Issue',
        renderer(cellData, {column, rowData}) {
            return <a href={rowData.jira_name} target="_blank">{cellData}</a>;
        },
    },
    {
        id: 'jira_assignee',
        type: DataType.String,
        header: 'JIRA Assignee',
    },
    {
        id: 'jira_create_date',
        type: DataType.Date,
        header: 'JIRA Create date',
    },
    {
        id: 'num_patches',
        type: DataType.Number,
        header: 'Open Patches',

    },
];

class SummaryTable extends React.Component {
    render() {
        const tableStyle = {
            marginTop: 30,
            overflow: "auto",
        };

        const h2Style = {
            margin: 5,
            marginTop: 15,
            fontSize: 25,
            textAlign: "center",
            fontFamily: "Helvetica",
            fontWeight: 100
        };

        return (
            <div style={tableStyle}>

                <h2 style={h2Style}>Summary of Patch Related JIRAs</h2>
                <hr width="15%" color={"#ddd"}/>
                <Request
                    url={url.URL_FETCH_DATA + 'patch/summary/' + this.props.jiraType}
                    method='get'
                    accept='application/json'
                >
                    {
                        ({error, result, loading}) => {
                            if (loading) {
                                return <div align="center">loading...</div>;
                            } else {
                                return (
                                    <div>
                                        <TacoTable columns={columns}
                                                   data={JSON.parse(result.body.responseMessage)}
                                                   striped
                                                   initialSortDirection={SortDirection.Descending}
                                                   initialSortColumnId="jira_create_date"/>
                                    </div>
                                )
                            }
                        }
                    }
                </Request>

            </div>
        );
    }
}

export default SummaryTable;
