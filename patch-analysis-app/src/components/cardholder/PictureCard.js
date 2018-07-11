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

export default class PictureCard extends React.Component {

    render() {

        const pictureCardStyle = {
            height: 100,
            width: 100,
            padding: 20,
            border: 10,
            borderColor: "#FFF",
            backgroundColor: "#000",
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
        const pictureStyle = {
            maxWidth: "100%",
            maxHeight: "100%",
            marginTop: this.props.marginTop,
        };

        return (
            <div style={pictureCardStyle} align="center">
                <a href={this.props.url} target={"_blank"}> <img src={this.props.imageName} style={pictureStyle}/> </a>
            </div>
        );
    }
}
