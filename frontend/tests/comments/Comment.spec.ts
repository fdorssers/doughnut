/**
 * @jest-environment jsdom
 */
import { flushPromises } from "@vue/test-utils";
import { screen } from "@testing-library/vue";
import NoteRealmAsync from "@/components/notes/NoteRealmAsync.vue";
import helper from "../helpers";
import makeMe from "../fixtures/makeMe";

helper.resetWithApiMock(beforeEach, afterEach);

describe("comments", () => {
  let note: Generated.NoteRealm;

  beforeEach(() => {
    helper.store.featureToggle = true;
    note = makeMe.aNoteRealm.please();
    const notesBulk: Generated.NotesBulk = {
      notePosition: makeMe.aNotePosition.please(),
      notes: [note],
    };
    helper.apiMock.expecting(`/api/notes/${note.id}`, notesBulk);
  });

  const queryToggleButton = () => {
    return screen.queryByRole("button", { name: "toggle comments" });
  };

  xit("should not display toggle comments button if there is no comments", async () => {
    helper.apiMock.expecting(`/api/notes/${note.id}/comments`, []);
    helper
      .component(NoteRealmAsync)
      .withProps({ noteId: note.id, expandChildren: false })
      .render();
    expect(queryToggleButton()).toBeNull();
  });

  it("fetch comments & render", async () => {
    const comment = { content: "my comment" };
    helper.apiMock.expecting(`/api/notes/${note.id}/comments`, [comment]);
    helper
      .component(NoteRealmAsync)
      .withProps({ noteId: note.id, expandChildren: false })
      .render();
    await flushPromises();
    queryToggleButton()?.click();
    await screen.findByText("my comment");
  });
});
