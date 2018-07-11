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
import url from '../../configuration';

export default class NumberCard extends React.Component {
    render() {
        const numberCardStyle = {
            height: 150,
            width: 150,
            padding: 20,
            border: 10,
            borderColor: "#FFF",
            backgroundColor: "#222",
            WebkitFilter: "drop-shadow(0px 0px 5px #666)",
            filter: "drop-shadow(0px 0px 5px #666)",
            marginWidth: 50,
            marginLeft: 15,
            marginRight: 15,
            marginTop: 30,
            textAlign: "center",
            align: "center",
            borderRadius: 30
        };
        const h1Style = {
            fontSize: 25,
            margin: 5,
            filter: "drop-shadow(0px 0px 5px #666)",
            color: "#FFA737",
            fontFamily: "Helvetica",
            fontWeight: 200,
            marginTop: "26%",
        };
        return (
            <Request
                url={url.URL_FETCH_DATA + 'patch/count/' + this.props.jiraType}
                method='get'
                accept='json'
            >
                {
                    ({error, result, loading}) => {
                        if (loading) {
                            return <div align="center"><p align="center">loading...</p></div>;
                        } else {
                            console.log(result);
                            return (
                                <div style={numberCardStyle}>
                                    <h1 style={h1Style}>
                                        {result.body.responseMessage} Ongoing {this.props.type} Patches
                                    </h1>
                                </div>
                            )
                        }
                    }
                }
            </Request>
        );
    }
}
