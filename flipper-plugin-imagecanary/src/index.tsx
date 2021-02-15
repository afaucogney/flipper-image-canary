import React from 'react';
import {PluginClient, usePlugin, createState, useValue, Layout} from 'flipper-plugin';
import {ManagedDataInspector, DetailSidebar} from 'flipper';
import { List, Typography, Divider } from 'antd';

type Data = {
  id: string;
  message?: string;
};

type Events = {
  newData: Data;
};

// Read more: https://fbflipper.com/docs/tutorial/js-custom#creating-a-first-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#pluginclient
export function plugin(client: PluginClient<Events, {}>) {
  const data = createState<Record<string, Data>>({}, {persist: 'data'});
  const selectedID = createState<string | null>(null, {persist: 'selection'});

  client.onMessage('newData', (newData) => {
    data.update((draft) => {
      draft[newData.id] = newData;
    });
  });

  client.addMenuEntry({
    action: 'clear',
    handler: async () => {
      data.set({});
    },
  });


  function setSelection(id: number) {
    selectedID.set('' + id);
    console.log("selected", id)
  }

  console.log("selected", selectedID)
  console.log("data", data)

  return {
    data,
    selectedID,
  };
}

// Read more: https://fbflipper.com/docs/tutorial/js-custom#building-a-user-interface-for-the-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#react-hooks
export function Component() {
  const instance = usePlugin(plugin);
  const data = useValue(instance.data);
  const selectedID = useValue(instance.selectedID);

  const onSelect = (selectedKeys, info) => {
      console.log('selected', selectedKeys);
      instance.setSelection(selectedKeys[0])
  };



   if (Object.keys(data).length < 1) {
         console.log("lf", data)
      return (
          <Layout.ScrollContainer>
                <p>Empty</p>
          </Layout.ScrollContainer>
      );
   } else {
      console.log("lf", Object.values(data))
      console.log("lf0", Object.values(data)[0])
      console.log("lf1", Object.values(data)[0][1])
       return (
            <Layout.ScrollContainer>
                <Divider orientation="left">Large Size</Divider>
                <List
                  size="large"
                  header={<div>Header</div>}
                  footer={<div>Footer</div>}
                  bordered
                  dataSource={Object.values(data)[0]}
                  renderItem={item => <List.Item>{renderItem(item)}</List.Item>}
                />
            </Layout.ScrollContainer>
       );

       function renderItem(item) {
          return Object.values(item)[0]
       }
   }
}
