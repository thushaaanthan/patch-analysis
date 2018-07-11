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

export default class Header extends React.Component {

    render() {
        const headerStyle = {
            backgroundColor: "#010101",
            width: "100%",
            height: 20,
            padding: 10,
        };
        const titleStyle = {
            fontFamily: "Helvetica",
            fontSize: 16,
            color: "#ffffff",
            textAlign: "left",
            fontWeight: 500,
        };
        return (
            <div style={headerStyle}>
                <div style={titleStyle}>
                    WSO2 Patch Information Dashboard
                </div>
            </div>
        );
    }
}
