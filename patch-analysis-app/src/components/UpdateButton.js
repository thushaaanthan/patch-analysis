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
import axios from 'axios';
import url from '../configuration';


class UpdateButton extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            data: 'Click to Update.'
        }
        this.updateState = this.updateState.bind(this);
    };

    updateState() {
        this.setState({data: 'Updating...'})
        axios.post(url.URL_FETCH_DATA + 'database').then(response => this.setState(
            {
                data: new Date().toLocaleTimeString() + ' : ' + response.data.responseMessage
            }))
    }

    render() {
        const buttonStyle = {
            marginTop: 20,
            fontSize: 20,
            width: 200,
            height: 60,
            fontFamily: "Helvetica",
            WebkitFilter: "drop-shadow(0px 0px 5px #666)",
            filter: "drop-shadow(0px 0px 5px #666)",
        };
        const dataStyle = {
            marginTop: 20,
            fontSize: 15,
            fontWeight: 100,
            fontFamily: "Helvetica",
            textAlign: "center",
        };
        return (
            <div>
                <table align="center" width="80%">
                    <tr align="center">
                        <td height="10%">
                            <button onClick={this.updateState} style={buttonStyle}>Update Data</button>
                        </td>
                    </tr>
                </table>
                <h1 style={dataStyle}>{this.state.data}</h1>
            </div>
        );
    }
}

export default UpdateButton;
