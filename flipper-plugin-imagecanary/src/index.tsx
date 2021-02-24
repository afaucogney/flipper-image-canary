import React from "react";
import {
  PluginClient,
  usePlugin,
  createState,
  useValue,
  Layout,
} from "flipper-plugin";
import { ManagedDataInspector, DetailSidebar } from "flipper";
import { List, Typography, Divider, Select } from "antd";

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
  const data = createState<Record<string, Data>>({}, { persist: "data" });
  const selectedID = createState<string | null>(null, { persist: "selection" });

  client.onMessage("newData", (newData) => {
    data.update((draft) => {
      draft[newData.id] = newData;
    });
  });

  client.addMenuEntry({
    action: "clear",
    handler: async () => {
      data.set({});
    },
  });

  function setSelection(id: number) {
    selectedID.set("" + id);
    console.log("selected", id);
  }

  console.log("selected", selectedID);
  console.log("data", data);

  return {
    data,
    selectedID,
    setSelection,
  };
}

// Read more: https://fbflipper.com/docs/tutorial/js-custom#building-a-user-interface-for-the-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#react-hooks
export function Component() {
  const instance = usePlugin(plugin);
  const data = useValue(instance.data);
  const selectedID = useValue(instance.selectedID);

  const onSelect = (selectedKeys: number) => {
    console.log("selected", selectedKeys);
    instance.setSelection(selectedKeys);
  };

  const children : Array<Option>= [
    <Option key="BITMAP_QUALITY_TOO_HIGH">Bitmap with too high memory</Option>,
    <Option key="BITMAP_QUALITY_TOO_LOW">Bitmap with too low quality</Option>,
    <Option key="INVISIBLE_BUT_MEMORY_OCCUPIED">Bitmap in memory but invisible</Option>
  ]

  if (Object.keys(data).length < 1) {
    console.log("lf", data);
    return renderLoading();
  } else {
    console.log("lf", Object.values(data));
    console.log("lf0", Object.values(data)[0]);
    return renderItemList(Object.values(data)[0], selectedID);
  }

  function renderLoading() {
    return (
      <Layout.ScrollContainer>
        <p>Empty</p>
      </Layout.ScrollContainer>
    );
  }

  function renderItemList(items: unknown[], selectedID: string) {
    console.log("lf2", items);
    let selectedItem = getSelectedItem(items, selectedID);
    return (
      <>
        {renderOptions()}
        <Layout.ScrollContainer>
          <Divider orientation="left">Large Size</Divider>
          <List
            size="large"
            header={<div>Image Issues</div>}
            footer={<div>Footer</div>}
            bordered
            dataSource={items}
            renderItem={(item) => (
              <List.Item
                key={item.imageViewHash}
                onClick={() => onSelect(item["imageViewHash"])}
              >
                {renderItem(item)}
              </List.Item>
            )}
          />
        </Layout.ScrollContainer>
        <DetailSidebar>{renderSidebar(selectedItem)}</DetailSidebar>
      </>
    );
  }

  function getSelectedItem(array: any[], selectedId: any) {
    let result = array.find((item: { [x: string]: any; }) => item["imageViewHash"] == selectedId);
    console.log("result: ", result);
    return result;
  }

  function renderItem(item: unknown) {
    console.log("item", item);
    return (
      <ul>
        <li>Issue type: {item["issueType"]}</li>
        <li>Activity class : {item["activityClass"]}</li>
        <li>Bitmap height: {item["bitmapHeight"]} pixels</li>
        <li>Bitmap width: {item["bitmapWidth"]} pixels</li>
        <li>ImageView Height: {item["imageViewHeight"]} pixels</li>
        <li>ImageView Width: {item["imageViewWidth"]} pixels</li>
        <li>
          {" "}
          <img
            src={"data:image/png;base64, " + item["base64"]}
            alt="App image"
          />{" "}
        </li>
        <li>Byte count: {item["byteCount"]} Megabytes</li>
        <li>Allocated byte count: {item["allocatedByteCount"]} Megabytes</li>
      </ul>
    );
  }

  function renderSidebar(item: unknown) {
    if (item == undefined){
      return (  <p>Empty</p>);
    } else {
    return (
      <Layout.Container gap pad>
        <Typography.Title level={4}>Extras</Typography.Title>
        <img src={"data:image/png;base64, " + item["base64"]} alt="App image" />
        {renderItem(item)}
        <ManagedDataInspector data={item["viewParents"]} expandRoot={true} />
      </Layout.Container>
    );
    }
  }

  function renderOptions() {
    return (
      <Select
        mode="multiple"
        allowClear
        style={{ width: "100%" }}
        placeholder="Please select"
        defaultValue={[]}
        onChange={handleChange}
      >
        {children}
      </Select>
    );
  }

  function handleChange(value: any) {
    console.log(`selected ${value}`);
  }
}
