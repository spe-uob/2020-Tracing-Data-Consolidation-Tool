import React, { Component } from 'react';
import styles from "./ProgressBar.module.css"
import Filler from "./Filler"
import { backendBaseUrl } from '../../config';

class ProgressBar extends React.Component {

    constructor(props){
        super(props);
        this.state = {
            percentage: 0
        };
        this.eventSource = new EventSource(`${backendBaseUrl}/progress`);
    }

    componentDidMount(){
        this.eventSource.onmessage = e => this.UpdatePercentage(JSON.parse(e.data));
    }

    UpdatePercentage(data){
        console.log(data/15281) // DEBUG // this is raw no of rows, more work
        this.setState({
            percentage: data/15281 * 100
        });
        if (data === 15281) {
            this.props.onComplete();
            this.eventSource.close();
        }
    }

    render() {
        // TODO use props to pass percentage so can fetch data from outer component?
        return ( // TODO get rid of h4 surrounding progress bar
            <div>
                <h4 className={styles.progressBar}>
                    <Filler percentage={this.state.percentage}></Filler>
                </h4>
            </div>
        );
    }
}

export default ProgressBar;
