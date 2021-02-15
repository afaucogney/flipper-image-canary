import React from 'react';
import {PluginClient, usePlugin, createState, useValue, Layout} from 'flipper-plugin';
import {ManagedDataInspector, DetailSidebar} from 'flipper';

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

   console.log("lf", data)
   console.log("lf", data[0])

   if (Object.keys(data).length < 1) {
      return (
          <Layout.ScrollContainer>
                <p>Empty</p>
          </Layout.ScrollContainer>
      );
   } else {
       return (
         <>
            <Layout.ScrollContainer>
               <p>De la data</p>
                 <ManagedDataInspector data={data} expandRoot={true}  onSelect={onSelect}/>
            </Layout.ScrollContainer>
            <DetailSidebar>
                {selectedID && renderSidebar(data[0],selectedID)}
             </DetailSidebar>
                </>
       );
   }
      function renderSidebar(row: Row, selectedId :number) {
           console.log("row: ", row)
           console.log("id: ", selectedId)
           console.log("row: ", filter(row,selectedId))
         return (
           <Layout.Container gap pad>
             <Typography.Title level={4}>Extras</Typography.Title>
             <ManagedDataInspector data={row} expandRoot={true} />
           </Layout.Container>
         );
     }
}
