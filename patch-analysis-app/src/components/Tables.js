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
import DevTable from "./tables/DevTable";
import SigningTable from "./tables/SigningTable";
import InactiveTable from "./tables/InactiveTable";
import ReleasedTable from "./tables/ReleasedTable";
import SummaryTable from "./tables/SummaryTable";
import {Tab, Tabs, TabList, TabPanel} from 'react-tabs';
import 'react-tabs/style/react-tabs.css';

class Tables extends React.Component {
    render() {
        const tabStyle = {
            fontSize: 22,
            margin: 5,
            marginTop: 30,
            textAlign: "center",
            fontFamily: "Helvetica",
            fontWeight: 100,
        };

        return (
            <Tabs style={tabStyle}>
                <TabList>
                    <Tab> Customer </Tab>
                    <Tab> Proactive </Tab>
                </TabList>
                <TabPanel>
                    <Tabs style={tabStyle}>
                        <TabList>
                            <Tab> Development</Tab>
                            <Tab> Inactive JIRAs</Tab>
                            <Tab> Signing</Tab>
                            <Tab> Released</Tab>
                            <Tab> Summary</Tab>
                        </TabList>
                        <TabPanel>
                            <DevTable jiraType="customer"/>
                        </TabPanel>
                        <TabPanel>
                            <InactiveTable jiraType="customer"/>
                        </TabPanel>
                        <TabPanel>
                            <SigningTable jiraType="customer"/>
                        </TabPanel>
                        <TabPanel>
                            <ReleasedTable jiraType="customer"/>
                        </TabPanel>
                        <TabPanel>
                            <SummaryTable jiraType="customer"/>
                        </TabPanel>
                    </Tabs>
                </TabPanel>
                <TabPanel>
                    <Tabs style={tabStyle}>
                        <TabList>
                            <Tab> Development </Tab>
                            <Tab> Inactive JIRAS </Tab>
                            <Tab> Signing </Tab>
                            <Tab> Released </Tab>
                            <Tab> Summary </Tab>
                        </TabList>
                        <TabPanel>
                            <DevTable jiraType="proactive"/>
                        </TabPanel>
                        <TabPanel>
                            <InactiveTable jiraType="proactive"/>
                        </TabPanel>
                        <TabPanel>
                            <SigningTable jiraType="proactive"/>
                        </TabPanel>
                        <TabPanel>
                            <ReleasedTable jiraType="proactive"/>
                        </TabPanel>
                        <TabPanel>
                            <SummaryTable jiraType="proactive"/>
                        </TabPanel>
                    </Tabs>
                </TabPanel>
            </Tabs>
        );
    }
}

export default Tables;
