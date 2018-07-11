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
import React, {Component} from 'react';
import './App.css';
import Header from "./components/Header";
import Title from "./components/Title";
import CardHolder from "./components/CardHolder";
import UpdateButton from "./components/UpdateButton";
import Footer from "./components/Footer";
import Tables from "./components/Tables";
class App extends Component {

    render() {
        return (
            <div className="Patch Analysis">
                <Header/>
                <Title/>
                <UpdateButton/>
                <CardHolder/>
                <Tables/>
                <Footer/>
            </div>
        );
    }
}

export default App;
