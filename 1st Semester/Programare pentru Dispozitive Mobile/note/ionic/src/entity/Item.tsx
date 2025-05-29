import React, { memo } from "react";
import { ItemProps } from "./ItemProps";

type ItemPropsExtended = ItemProps & {
  onClick: (id: number) => void;
  selectedId?: number;
  deletedLocally: boolean;
};

const Item: React.FC<ItemPropsExtended> = ({
  text,
  onClick,
  date,
  id,
  selectedId,
  deletedLocally,
}) => {
  const isSelected = selectedId == id;

  return (
    <div
      style={{
        margin: "5px",
        backgroundColor:
          isSelected && deletedLocally
            ? "red"
            : isSelected
            ? "blue"
            : "transparent",
        cursor: "pointer",
      }}
      onClick={() => {
        typeof id == "number" && onClick(id);
      }}
    >
      {text} - {new Date(date).toLocaleDateString()}
    </div>
  );
};

export default memo(Item);
